package prop.gen

import prop.state.State
import prop.stream.Stream
import prop.parallelism._
import java.util.concurrent._

/**
  * A generator implement with `RNG`. It can generate the datas which we need and it is infinite.
  * In this class, we will do some data struct conver
  * @Author How
  */
case class Gen[A](sample: State[RNG, A]) {

  /**
    * A boolean generator
    */
  def boolean: Gen[Boolean] = Gen(RNG.boolean)

  def double: Gen[Double] = Gen(RNG.double)

  /**
    * It will generate a `List[A]`, which its size is assigned by `n`, but its content is generated by `g`
    * @param n The size of the `List`
    * @param g The content generator of the `List`
    */
  def listOfN(n: Int, g: Gen[A]): Gen[List[A]] =
    Gen(State.sequence(List.fill(n)(g.sample)))

  def map[B](f: A => B): Gen[B] = Gen(sample.map(f))

  def flatMap[B](f: A => Gen[B]): Gen[B] = Gen(sample.flatMap(a => f(a).sample))

  /**
    * Generating different size `List` by size. You can let size range with(start, stop), then pass it in
    * this function, it will generate list whose size is in start and stop.
    * You can call Gen.choose(stop, stopExclusive) to generate a limited int number.
    *
    * @param size The size generator you can assigned
    */
  def listOfN(size: Gen[Int]): Gen[List[A]] = size.flatMap { n =>
    listOfN(n, this)
  }

  /**
    * The function can make sure generate a not empty list
    */
  def lisfOf1(size: Gen[Int]): Gen[List[A]] = size.flatMap { n =>
    listOfN((1 max n), this)
  }

  def union[A](g1: Gen[A], g2: Gen[A]): Gen[A] =
    boolean.flatMap(b => if (b) g1 else g2)

  /**
    * choose genenrator by weight
    */
  def weighted[A](g1: (Gen[A], Double), g2: (Gen[A], Double)): Gen[A] = {
    val g1Shreshould = g1._2 / (g1._2 + g2._2)
    Gen(RNG.double).flatMap(g => if (g > g1Shreshould) g1._1 else g2._1)
  }

  def unsized: SGen[A] = SGen(_ => this)

  def map2[B, C](g: Gen[B])(f: (A, B) => C): Gen[C] =
    Gen(sample.map2(g.sample)(f))

  def **[B](g: Gen[B]): Gen[(A, B)] = this.map2(g)(_ -> _)

}

object Gen {

  /**
    * Choose a limited generator
    */
  def choose(start: Int, stopExclusive: Int): Gen[Int] =
    Gen(RNG.nextInt(start, stopExclusive))

  def unit[A](a: => A): Gen[A] = Gen(State.unit[RNG, A](a))

  def double: Gen[Double] = Gen(RNG.double)

  /**
    * It is same with above
    */
  def listOfN[A](size: Int, g: Gen[A]): Gen[List[A]] =
    Gen(State.sequence(List.fill(size)(g.sample)))

  /**
    * It is same with above
    */
  def listOfN[A](size: Gen[Int], g: Gen[A]): Gen[List[A]] =
    size.flatMap(s => listOfN(s, g))

  /**
    * Convert a generator to Stream, but this Stream is not Scala autogenetic Stream, it is our restructuring Stream,
    * It has some functions with Scala autogenetic Stream, you can refer it to Scala autogenetic Stream.
    * You can get detail in `prop.stream.Stream`
    */
  def run[A](g: Gen[A]): Stream[A] = {
    val rng = RNG.get
    Stream.unfold(rng)(rng => Some(g.sample.run(rng)))
  }

  def stringN(n: Int): Gen[String] =
    listOfN(n, choose(0, 127)).map(_.map(_.toChar).mkString)

  /**
    * generate odd numbers with limited
    */
  def odd(start: Int, stopExclusive: Int): Gen[Int] =
    choose(start, stopExclusive).map { r =>
      if (r % 2 == 0) r + 1 else r
    }

  /**
    * generate even numbers with limited
    */
  def even(start: Int, stopExclusive: Int): Gen[Int] =
    choose(start, stopExclusive).map { r =>
      if (r % 2 != 0) r + 1 else r
    }

  /**
    * Par is parallelism data struct, which represent `ExectorService` => `Future[A]`
    * The `Future` is not Scala autogenetic `Future`, but it is pure functional, you can get
    * detail in `prop.parallelism`
    */
  type Par[A] = (ExecutorService) => prop.parallelism.Future[A]

  /**
    * generate `Par[Int]`, Par is parallelism data struct
    */
  def pint2: Gen[Par[Int]] = choose(-100, 100).listOfN(choose(0, 20)).map { l =>
    l.foldLeft[Par[Int]](NoBlockPar.unit(0))((p, i) =>
      NoBlockPar.fork { NoBlockPar.map2(p, NoBlockPar.unit(i))(_ + _) })
  }

  def genStringFn[A](g: Gen[A]): Gen[String => A] = Gen {
    State { (rng: RNG) =>
      val (seed, rng2) = rng.nextInt.run(rng)
      val f =
        (s: String) => g.sample.run(RNG(seed.toLong ^ s.hashCode.toLong))._1
      (f, rng2)
    }
  }
}
