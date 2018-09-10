package test.prop.gen

import org.scalatest.FlatSpec
import prop.gen._

class OddAndEvenSpec extends FlatSpec {
  "test produce ood" should "success" in {
    Gen.run(Gen.odd(0, 100)).take(10).toList.map(r => assert(r % 2 != 0))
  }

  "test produce even" should "success" in {
    Gen.run(Gen.even(0, 100)).take(100).toList.map(r => assert(r % 2 == 0))
  }

}
