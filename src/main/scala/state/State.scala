package prop.state

case class State[S, +A](run: S => (A, S)) {
  def flatMap[B](f: A => State[S, B]): State[S, B] = {
    State(
      s => {
        val (a, s1) = run(s)
        f(a).run(s1)
      }
    )
  }

  def map[B](f: A => B): State[S, B] = flatMap { i =>
    State(
      s => {
        f(i) -> s
      }
    )
  }

  def map2[B, C](b: State[S, B])(f: (A, B) => C): State[S, C] = flatMap { i =>
    State(
      s => {
        val (vb, rb) = b.run(s)
        f(i, vb) -> rb
      }
    )
  }
}

object State {
  def unit[S, A](a: A): State[S, A] = State(s => (a, s))
  def sequence2[A, S](as: List[State[S, A]]): State[S, List[A]] =
    as.foldRight(unit[S, List[A]](List()))((a, b) => a.map2(b)(_ :: _))

  def sequence[A, S](as: List[State[S, A]]): State[S, List[A]] =
    as.foldLeft(unit[S, List[A]](List()))((b, a) => b.map2(a)((x, y) => y :: x))

  def modify[S](f: S => S): State[S, Unit] =
    for {
      s <- get
      _ <- set(f(s))
    } yield ()

  def get[S]: State[S, S] = State(s => (s, s))

  def set[S](s: S): State[S, Unit] = State(_ => ((), s))
}
