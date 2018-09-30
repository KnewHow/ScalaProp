Scala-test-prop  Scala Test Case Generator And Exector
=====================================================================================================
[![Build Status](https://travis-ci.org/KnewHow/ScalaProp.svg?branch=master)](https://travis-ci.org/KnewHow/ScalaProp)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4a39a5203267439ebe30d4d060cb2bfa)](https://www.codacy.com/app/KnewHow/ScalaProp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=KnewHow/ScalaProp&amp;utm_campaign=Badge_Grade)

Scala-tes-prop provides test cases generator then run test function with them. Finally it will println test result information and return a boolean value to tell you test result which can be asserted by scala test.

## Generator
Generator is basic component in scala-test-prop, which can generate test cases you want. For example, if you want to get a range integer, you see following code:
```Scala
import prop.gen.Gen

// return a Gen[Int] with integer range in (1,10), but the range exclusive 10
Gen.choose(1, 10)
```
It also can generate other generator, such as `Gen.odd(10,100)`, `Gen.listOf(10, Gen.choose(10,100))`. The first function will return random odd integer from 10 to 100, but inclusive 100. The second function will return a list with 10 size whose
elements range is between 10 and 100 but exclusive 100. You can find more in [Object Gen](https://github.com/KnewHow/ScalaProp/blob/master/src/main/scala/prop/Gen.scala)

## Executor
Now, you have knew how to obtain a generator, but how to run testing function with them? The Executor match to `Gen` is `Prop`. `Prop` is running parameters to testing result container. You can do `&&` or `||` with two `Prop`, the implement you can refer: https://github.com/KnewHow/ScalaProp/blob/master/src/main/scala/prop/Prop.scala

You can call `Prop.test(n,rng)` to get test result. The first parameter is how many test cases will be taken to test. The second parameter is random number generator. You can get it by `RNG.get`. We have provided some default value to make you use it eaisily.

If you want to test a sorted function, you can generate a `Gen[List[A]]` first, then test it by `Prop`, you can refer following code:
```Scala
package test.prop.gen
import org.scalatest.FlatSpec
import prop.gen._

class ListOfFixedSizeSpec extends FlatSpec {
  "test listOf with fixed size" should "success" in {
    // Generate list with 20 size whose element is between 10 and 100
    val g = Gen.listOfN(20, Gen.choose(10, 100))
    val p = Prop.forAll(g) { r =>
      // Writing what you want to test, make sure it last result is boolean.
      val s = r.sorted
      val h = s.headOption
      h.map(rs => !r.exists(_ < rs)).getOrElse(true)
    }
    // In default, test function will run checked function with 10 test cases.
    assert(p.test())
  }
}
```
Because test function return a boolean value, if the value is true, representing the all test cases passed, you can combine it with scala-test assert. At same time, `Prop.test` also println some test information in console. If all test cases passed, it will print `[info] OK, 10 testcases passed`, otherwise will print `[error] test case failure, case by...,But success n times`.

you can also generate a list with random size by `Gen.listOfN(Gen.choose(10, 20), Gen.choose(10, 100))`.

## Incremental Testing
Sometimes, once or twice testing don't test whether a bug in checked function. We may want a test way, which can increase the test cases gradually until run enough times we assigned. `SProp` is born in time!

For example, If you want to test a sorted function and want to test it 20 times with test cases increasing gradually. You can choose `SProp`, you can refer following code:
```Scala
package test.prop.sgen

import org.scalatest.FlatSpec
import prop.gen._

class SPropFixedStepSpec extends FlatSpec {
  "test SProp with sorted function by fixed step increasing" should "success" in {
    // geneate a list with random size whose element is random
    val g = Gen.listOfN(Gen.choose(100, 200), Gen.choose(300, 400))
    // conver Gen into SGen, it is very simply
    val sg = g.unsized
    // similar with Prop.forAll, just return a boolean result
    val p = SProp.forAll(sg) { r =>
      val s = r.sorted
      val h = s.headOption
      h.map(rs => !r.exists(_ < rs)).getOrElse(true)
    }
    // run the checked function
    assert(
      p.test(
        // the minimal test cases will be run first
        minTestCase = 10,
        // the step the test cases will increase by
        step = 1,
        // how many time the function will be run
        testTimes = 20,
        // whether use random step
        randomStep = false
      ))
  }
}
```

The function will take 10 test cases first, then increase test cases by step 1 unitl it run 20 times. In this way, it will reduce testing fortuity.

But sometimes, fixed step could be stiff, So we also prepare a random step between 1 and n which you assigned. You just need
assign `randomStep=true`, we will genereate random step between 1 and `n`

We has give some default parameters to `test` function, So you can not pass any parameters to `test` function.

## Bugs
The `Gen` is so easy and elegant, but it has some bugs:
If you want to test `Gen.listOfN` with a big size list, it will throw `stack overflow`. The tempoary  solution is you can increase stack size by jvm prameters with `-Xss500M`. You can write it in a file named `.jvmopts` in sbt project root directory.

I will try to fix it in the future!
## How to Get
