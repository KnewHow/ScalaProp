package prop.gen

import prop.parallelism._
import java.util.concurrent._

/**
  * Incremental test tool.
  * In `Prop`, it will take n testcases then test them. it is a basic test tool.
  * In this `SProp`, we will increase test cases amount by step at each tests.
  * So, you could assign the minimal value of test cases and the step to increase.
  * At same time, you could assign the times the test cases run.
  * For example:
  *   you assigin minTestCase = 10 step=1, testTimes=5, SProp will test it by test cases which length from 11 to 15.
  *
  * But Some times, the fixed step may leave out some test cases, for example,minTestCase=5, step=2,it will test all ood
  * test cases. So we prepare a parameter to set whether can random step. If you set the value of the parameter is true,
  * We will generate dynamic step each run.
  *
  * Sprop property represent minTestCase, step, testTimes, randomStep,RNG ordinal.
  * @Author KnewHow 2018-09-23
  */
case class SProp(run: (Int, Int, Int, Boolean, RNG) => Result) {

  /**
    * run SProp then return boolean result.
    * @param minTestCase The minimal value test cases
    * @param step The ascending step
    * @param testTimes How many times the test cases run
    * @randomStep Whether generate step randow
    * @rng A random generator
    * return If all test cases passed, true will be returned, otherwise false will be returned.
    */
  def test(
      minTestCase: Int = 100,
      step: Int = 1,
      testTimes: Int = 100,
      randomStep: Boolean = true,
      rng: RNG = RNG(System.currentTimeMillis)
  ): Boolean = this.run(minTestCase, step, testTimes, randomStep, rng) match {
    case Passed =>
      println(s"[info] OK, all testCases passed")
      true
    case Falsified(msg, sc) =>
      println(s"[error] test case failure, case by $msg, But success $sc times")
      false
  }

  /**
    * Do && with two SProp,if both SProp passed, return new SProp with Passed result,
    * otherwise return Failure as result.
    */
  def &&(sp: SProp): SProp = SProp {
    (minTestCase, step, testTimes, randomStep, rng) =>
      (this.run(minTestCase, step, testTimes, randomStep, rng),
       sp.run(minTestCase, step, testTimes, randomStep, rng)) match {
        case (Passed, Passed)       => Passed
        case (f: Falsified, Passed) => f
        case (Passed, f: Falsified) => f
        case (f1: Falsified, f2: Falsified) =>
          Falsified(f1.failure + "," + f2.failure, f1.successes + f2.successes)
      }
  }

  /**
    * Do || with two SProp, if one of Passed, it will return Passed result,
    * otherwise return failure result.
    */
  def ||(sp: SProp): SProp = SProp {
    (minTestCase, step, testTimes, randomStep, rng) =>
      (this.run(minTestCase, step, testTimes, randomStep, rng),
       sp.run(minTestCase, step, testTimes, randomStep, rng)) match {
        case (Falsified(f1, s1), Falsified(f2, s2)) =>
          Falsified(f1 + "," + f2, s1 + s2)
        case _ => Passed
      }
  }
}

object SProp {

  /**
    * Ascending forAll function to check tested function.
    * You need give Int => Gen[A] function to generate different generator and a test assert.
    */
  def forAll[A](sg: SGen[A])(f: A => Boolean): SProp = forAll(sg.forSize)(f)

  /**
    * Ascending forAll function to check tested function.
    * You need give Int => Gen[A] function to generate different generator and a test assert.
    */
  def forAll[A](g: Int => Gen[A])(f: A => Boolean): SProp = SProp {
    (minTestCase, step, testTimes, randomStep, rng) =>
      {
        val props: Stream[(Int, Prop)] =
          getTestCaseAmount(minTestCase, step, randomStep)
            .take(testTimes)
            .map(i => i -> Prop.forAll(g(i))(f))
        val sp: SProp = props
          .map(p =>
            SProp { (minTestCase, step, testTimes, randomStep, rng) =>
              p._2.run(p._1, rng)
          })
          .toList
          .reduce(_ && _)
        sp.run(minTestCase, step, testTimes, randomStep, rng)
      }
  }

  def check(p: => Boolean): SProp = SProp { (_, _, _, _, _) =>
    if (p) Passed else Falsified("()", 0)
  }

  /**
    * Obtainning test cases amount,If you set random step, we will random generate step
    * by step you give, the range of the random step is from 1 to `step`
    * @minTestCase The minimal value of min test cases
    * @step The step you counld set
    * @randomStep Whether random step by give step, true will random, otherwise use fixed step
    * @return Return a stream of test cases length
    */
  def getTestCaseAmount(minTestCase: Int,
                        step: Int,
                        randomStep: Boolean): Stream[Int] = randomStep match {
    case true  => Stream.iterate(minTestCase)(_ + getRandomStep(step))
    case false => Stream.iterate(minTestCase)(_ + step)
  }

  /**
    * Obtain random step with RNG
    */
  def getRandomStep(step: Int): Int = RNG.nextInt(1, step + 1).run(RNG.get)._1

  /**
    * Following method is used to test parallelism pragrom,
    * Now, it is experimental, We may complete in the future
    */
  val S = SGen.weighted[ExecutorService](
    SGen.choose(1, 10).map(Executors.newFixedThreadPool) -> .75,
    SGen.unit(Executors.newCachedThreadPool) -> .25
  )

  type Par[A] = (ExecutorService) => prop.parallelism.Future[A]
  def forAllPar[A](g: SGen[A])(f: A => Par[Boolean]): SProp = forAll(S ** g) {
    case (e, a) => NoBlockPar.run(e)(f(a))
  }
}
