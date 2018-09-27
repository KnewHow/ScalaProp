package test.prop.sgen

import org.scalatest.FlatSpec
import prop.gen._

class SPropFixedStepSpec extends FlatSpec {
  "test SProp with sorted function by fixed step increasing" should "success" in {
    val g = Gen.listOfN(Gen.choose(100, 200), Gen.choose(300, 400))
    val sg = g.unsized
    val p = SProp.forAll(sg) { r =>
      val s = r.sorted
      val h = s.headOption
      h.map(rs => !r.exists(_ < rs)).getOrElse(true)
    }
    assert(
      p.test(
        minTestCase = 10,
        step = 1,
        testTimes = 20,
        randomStep = false
      ))
  }
}
