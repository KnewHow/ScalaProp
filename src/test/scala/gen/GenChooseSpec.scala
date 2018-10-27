package test.prop.gen

import org.scalatest.FlatSpec
import prop.gen._

class GenChooseSpec extends FlatSpec {
  "test Gen choose function" should "succeed" in {
    val g = Gen.choose(0, 2)
    val p = Prop.forAll(g) { r =>
      r >= 0 && r < 2
    }
    p.test(n = 1000)

  }
}
