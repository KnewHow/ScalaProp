package prop.gen

import prop.stream.Stream

sealed trait Result {
  def isFalsified: Boolean
}

case object Passed extends Result {
  def isFalsified = false
}

case class Falsified(failure: String, successes: Int) extends Result {
  def isFalsified = true
}

case class Prop(run: (Int, RNG) => Result) {
  def test(n: Int, rng: RNG): Boolean = this.run(n, rng) match {
    case Passed =>
      println(s"OK, $n testcases passed")
      true
    case f: Falsified =>
      println(
        s"test case failure, case by ${f.failure}, But success ${f.successes} times")
      false
  }

  def &&(p: Prop): Prop = Prop { (n, rng) =>
    (this.run(n, rng), p.run(n, rng)) match {
      case (Passed, Passed)       => Passed
      case (f: Falsified, Passed) => f
      case (Passed, f: Falsified) => f
      case (f1: Falsified, f2: Falsified) =>
        Falsified(f1.failure + "," + f2.failure, f1.successes + f2.successes)
    }
  }

  def ||(p: Prop): Prop = Prop { (n, rng) =>
    (this.run(n, rng), p.run(n, rng)) match {
      case (Falsified(f1, s1), Falsified(f2, s2)) =>
        Falsified(f1 + "," + f2, s1 + s2)
      case _ => Passed
    }
  }
}

object Prop {
  def forAll[A](as: Gen[A])(f: A => Boolean): Prop = Prop { (n, rng) =>
    {
      randomStream(as)(rng)
        .zip(Stream.from(0))
        .take(n)
        .map {
          case (a, i) =>
            try {
              if (f(a)) Passed else Falsified(a.toString, i)
            } catch { case e: Exception => Falsified(buildMsg(a, e), i) }
        }
        .find(_.isFalsified)
        .getOrElse(Passed)
    }
  }
  def randomStream[A](g: Gen[A])(rng: RNG): Stream[A] =
    Stream.unfold(rng)(rng => Some(g.sample.run(rng)))

  def buildMsg[A](s: A, e: Exception): String =
    s"test case $s\n" +
      s"generated an exception: ${e.getMessage}\n" +
      s"stack trace:\n ${e.getStackTrace.mkString("\n")}"

}
