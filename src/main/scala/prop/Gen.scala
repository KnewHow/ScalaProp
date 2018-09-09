package fpscala.testing

import prop.state.State
import prop.parallelism._
import java.util.concurrent._
case class Gen[A](sample: State[RNG, A]) {
  def boolean: Gen[Boolean] = Gen(RNG.boolean)

  def double: Gen[Double] = Gen(RNG.double)

  def listOfN(n: Int, g: Gen[A]): Gen[List[A]] =
    Gen(State.sequence(List.fill(n)(g.sample)))

  def map[B](f: A => B): Gen[B] = Gen(sample.map(f))

  def flatMap[B](f: A => Gen[B]): Gen[B] = Gen(sample.flatMap(a => f(a).sample))

  def lisfOf1(size: Gen[Int]): Gen[List[A]] = size.flatMap { n =>
    listOfN((1 max n), this)
  }

  def listOfN(size: Gen[Int]): Gen[List[A]] = size.flatMap { n =>
    listOfN(n, this)
  }
  def union[A](g1: Gen[A], g2: Gen[A]): Gen[A] =
    boolean.flatMap(b => if (b) g1 else g2)

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
  def choose(start: Int, stopExclusive: Int): Gen[Int] =
    Gen(RNG.nextInt(start, stopExclusive))

  def unit[A](a: => A): Gen[A] = Gen(State.unit[RNG, A](a))

  def double: Gen[Double] = Gen(RNG.double)
  type Par[A] = (ExecutorService) => prop.parallelism.Future[A]

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
