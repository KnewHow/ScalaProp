package test.prop.sgen

import org.scalatest.FlatSpec
import prop.gen._

class SPropSpec extends FlatSpec {
  "test ForAll function" should "success" in {
    val g = Gen.choose(10, 100)
    val sg = g.unsized
    val r = SProp.forAll(sg)(_ < 100)
    assert(r.test(20, 20))
  }
}
