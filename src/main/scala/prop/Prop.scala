package prop.gen

import prop.stream.Stream

/**
  * Test result trait, it has `isFalsified` property to represent
  * test rusult is success or failure. subclass can override
  * the property to represent differen result.
  */
sealed trait Result {
  def isFalsified: Boolean
}

/**
  * It represents all test cases passed
  */
case object Passed extends Result {
  def isFalsified = false
}

/**
  * It represents some test cases failure, and `failure` represents failure reason and `successes` represent successful test case amount.
  */
case class Falsified(failure: String, successes: Int) extends Result {
  def isFalsified = true
}

/**
  * Test prop, hold the test cases generator and checked function. You can call `test` functions to get final test result.
  * @Author How
  */
case class Prop(run: (Int, RNG) => Result) {

  /**
    * Run all test cases and checked funtions, then println test result and ruturn boolean tag.
    * If all test cases passed, It will print [OK, $n testcases passed] and return true,
    * otherwise will print [test case failure...] with the reasons of failure and successful amout.
    * In order to run, you must assign `RNG` and the times of run.
    * @param n The times of run checked function
    * @param rng The rng to run generator
    * @return If all test cases passed, return true, otherwise return false.
    */
  def test(n: Int = 10, rng: RNG = RNG.get): Boolean = this.run(n, rng) match {
    case Passed =>
      println(s"[info] OK, $n testcases passed")
      true
    case f: Falsified =>
      println(
        s"[info] test case failure, case by ${f.failure}, But success ${f.successes} times")
      false
  }

  /**
    * Combine two props with &&, if both of them are passed, it will return Passed as the Prop result,
    * otherwise will return Falsified as the Prop result, and combine them failures.
    */
  def &&(p: Prop): Prop = Prop { (n, rng) =>
    (this.run(n, rng), p.run(n, rng)) match {
      case (Passed, Passed)       => Passed
      case (f: Falsified, Passed) => f
      case (Passed, f: Falsified) => f
      case (f1: Falsified, f2: Falsified) =>
        Falsified(f1.failure + "," + f2.failure, f1.successes + f2.successes)
    }
  }

  /**
    * Combine two props with ||, if one of them is passed, it will return Passed as the Prop result.
    * If both of them are failed, Falsified will be a result.
    */
  def ||(p: Prop): Prop = Prop { (n, rng) =>
    (this.run(n, rng), p.run(n, rng)) match {
      case (Falsified(f1, s1), Falsified(f2, s2)) =>
        Falsified(f1 + "," + f2, s1 + s2)
      case _ => Passed
    }
  }
}

object Prop {

  /**
    * Inspection funcion to check the function you want to test.
    * In order to run it, you must give a generator of `A` and a tested function.
    * In this function, we just build a test relation, all test cases are not be run until to call this function then run its. You can get detail in `ForAllSpec`
    * return value.
    * @param as Test cases generator
    * @param f Tested function
    * @return A test prop hold test cases and relation, you can call its test method to generate test result
    */
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

  /**
    * generate test cases with g and rng
    */
  def randomStream[A](g: Gen[A])(rng: RNG): Stream[A] =
    Stream.unfold(rng)(rng => Some(g.sample.run(rng)))

  /**
    * combine failure infos
    */
  def buildMsg[A](s: A, e: Exception): String =
    s"test case $s\n" +
      s"generated an exception: ${e.getMessage}\n" +
      s"stack trace:\n ${e.getStackTrace.mkString("\n")}"

}
