package test.prop.gen

import org.scalatest.FlatSpec
import prop.gen._

class ForAllSpec extends FlatSpec {
  "test sort function" should "success" in {
    val l = Gen.listOfN(Gen.choose(100, 500), Gen.choose(0, 1000))
    val r = Prop.forAll(l) { list =>
      val sortedL = list.sorted
      val head = sortedL.headOption
      val r: Option[Boolean] = head.map(rs => !sortedL.exists(_ < rs))
      r.getOrElse(true)
    }
    assert(r.test(100, RNG.get))
  }
}
