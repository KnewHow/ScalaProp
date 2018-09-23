package test.prop.sgen

import org.scalatest.FlatSpec
import prop.gen._

class SPropSpec extends FlatSpec {
  "test ForAll function with fixed step" should "success" in {
    val g = Gen.choose(10, 100)
    val sg = g.unsized
    val r = SProp.forAll(sg)(_ < 100)
    assert(
      r.test(
        minTestCase = 10,
        step = 1,
        testTimes = 20,
        randomStep = false
      ))
  }

  "test ForAll function with random step" should "success" in {
    val g = Gen.choose(10, 100)
    val sg = g.unsized
    val r = SProp.forAll(sg)(_ < 100)
    assert(
      r.test(
        minTestCase = 10,
        step = 5,
        testTimes = 20
      ))
  }
}
