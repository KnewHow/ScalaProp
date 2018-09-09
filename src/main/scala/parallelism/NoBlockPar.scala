package prop.parallelism

import java.util.concurrent._
object NoBlockPar {
  type Par[A] = (ExecutorService) => Future[A]
  def unit[A](a: A): Par[A] =
    (es: ExecutorService) =>
      new Future[A] {
        def apply(cb: A => Unit): Unit = cb(a)
    }

  def run[A](es: ExecutorService)(p: Par[A]): A = {
    val ref = new java.util.concurrent.atomic.AtomicReference[A]()
    val count = new CountDownLatch(1)
    p(es) { a =>
      ref.set(a); count.countDown
    }
    count.await()
    ref.get
  }

  def delay[A](a: => A): Par[A] =
    (ex: ExecutorService) =>
      new Future[A] {
        def apply(cb: A => Unit): Unit = cb(a)
    }

  def fork[A](a: => Par[A]): Par[A] =
    (es: ExecutorService) =>
      new Future[A] {
        def apply(cb: A => Unit): Unit = eval(es)(a(es)(cb))
    }

  def eval(es: ExecutorService)(r: => Unit): Unit =
    es.submit(new Callable[Unit] { def call = r })

  def lazyUnit[A](a: A): Par[A] = fork(unit(a))

  def asyncF[A, B](f: A => B): A => Par[B] = (a: A) => lazyUnit(f(a))

  def map2[A, B, C](p: Par[A], p1: Par[B])(f: (A, B) => C): Par[C] =
    (es: ExecutorService) =>
      new Future[C] {
        def apply(cb: C => Unit): Unit = {
          var ar: Option[A] = None
          var br: Option[B] = None
          val map2Actor = Actor[Either[A, B]](es) {
            case Left(a) =>
              br match {
                case None    => ar = Some(a)
                case Some(b) => eval(es)(cb(f(a, b)))
              }

            case Right(b) =>
              ar match {
                case None    => br = Some(b)
                case Some(a) => eval(es)(cb(f(a, b)))
              }
          }

          p(es) { a =>
            map2Actor ! Left(a)
          }
          p1(es) { b =>
            map2Actor ! Right(b)
          }
        }
    }

  def map[A, B](p: Par[A])(f: A => B): Par[B] =
    map2(p, unit(()))((a, _) => f(a))

  def sequence[A](ls: List[Par[A]]): Par[List[A]] =
    ls.foldRight[Par[List[A]]](unit(List()))((a, b) => map2(a, b)(_ :: _))

  def equals[A](es: ExecutorService)(p1: Par[A], p2: Par[A]): Boolean =
    run(es)(p1) == run(es)(p2)

  def equals[A](p1: Par[A], p2: Par[A]): Par[Boolean] = map2(p1, p2)(_ == _)

  def parMap[A, B](ls: List[A])(f: A => B): Par[List[B]] = fork {
    val fbs: List[Par[B]] = ls.map(asyncF(f))
    sequence(fbs)
  }

  def parFilter[A](ls: List[A])(f: A => Boolean): Par[List[A]] = {
    val fbs: List[Par[List[A]]] = ls.map {
      asyncF(r => if (f(r)) List(r) else List())
    }
    map(sequence(fbs))(_.flatten)
  }

}

sealed trait Future[A] {
  private[parallelism] def apply(a: A => Unit): Unit
}
