package prop.gen

import prop.stream.Stream
import prop.parallelism._
import java.util.concurrent._

case class SProp(run: (Int, Int) => Result) {
  def test(
      maxSize: Int = 100,
      testCases: Int = 100,
  ): Boolean = this.run(maxSize, testCases) match {
    case Passed =>
      println(s"[info] OK, $testCases testCases passed")
      true
    case Falsified(msg, sc) =>
      println(s"[info] test case failure, case by $msg, But success $sc times")
      false
  }

  def &&(sp: SProp): SProp = SProp { (maxSize, testCases) =>
    (this.run(maxSize, testCases), sp.run(maxSize, testCases)) match {
      case (Passed, Passed)       => Passed
      case (f: Falsified, Passed) => f
      case (Passed, f: Falsified) => f
      case (f1: Falsified, f2: Falsified) =>
        Falsified(f1.failure + "," + f2.failure, f1.successes + f2.successes)
    }
  }

  def ||(sp: SProp): SProp = SProp { (maxSize, testCases) =>
    (this.run(maxSize, testCases), sp.run(maxSize, testCases)) match {
      case (Falsified(f1, s1), Falsified(f2, s2)) =>
        Falsified(f1 + "," + f2, s1 + s2)
      case _ => Passed
    }
  }
}

object SProp {
  def forAll[A](sg: SGen[A])(f: A => Boolean): SProp = forAll(sg.forSize)(f)

  def forAll[A](g: Int => Gen[A])(f: A => Boolean): SProp = SProp {
    (maxSize, testCases) =>
      {
        val casesPerSize = (testCases + (maxSize - 1) / maxSize)
        val props: Stream[Prop] = Stream
          .from(0)
          .take((maxSize min casesPerSize) + 1)
          .map(i => Prop.forAll(g(i))(f))
        val sp: SProp = props
          .map(p =>
            SProp { (maxSize, casesPerSize) =>
              p.run(maxSize, RNG.get)
          })
          .toList
          .reduce(_ && _)
        sp.run(maxSize, testCases)
      }
  }

  def check(p: => Boolean): SProp = SProp { (_, _) =>
    if (p) Passed else Falsified("()", 0)
  }

  val S = SGen.weighted[ExecutorService](
    SGen.choose(1, 10).map(Executors.newFixedThreadPool) -> .75,
    SGen.unit(Executors.newCachedThreadPool) -> .25
  )

  type Par[A] = (ExecutorService) => prop.parallelism.Future[A]
  def forAllPar[A](g: SGen[A])(f: A => Par[Boolean]): SProp = forAll(S ** g) {
    case (e, a) => NoBlockPar.run(e)(f(a))
  }
}
