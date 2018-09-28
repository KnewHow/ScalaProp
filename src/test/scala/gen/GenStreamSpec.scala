package test.prop.gen

import org.scalatest.FlatSpec
import prop.gen._

class GenStreamSpec extends FlatSpec {
  "test gen to stream" should "success" in {
    val g = Gen.listOfN(Gen.choose(10000, 50000), Gen.choose(5000, 10000))
    val r = Gen.run(g).take(1).toList
    assert(true)
  }
}
