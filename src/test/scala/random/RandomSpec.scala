package test.prop.random

import prop.random.Random
import prop.gen._
import org.scalatest.FlatSpec

class RandomSpec extends FlatSpec {
  "test random choose(b,e)" should "succeed" in {
    val p = Prop.forAll(
      for {
        b <- Gen.choose(0, 1000000)
        e <- Gen.choose(10000000, 20000000)
      } yield (b, e)
    ) {
      case (b, e) =>
        val r = Random.choose(b, e)
        r >= b && r <= e
    }
    assert(p.test(10000))
  }

  "test random choose(b,e,size)" should "succeed" in {
    val p = Prop.forAll(
      for {
        b <- Gen.choose(0, 1000000)
        e <- Gen.choose(10000000, 20000000)
        size <- Gen.choose(1000, 10000)
      } yield (b, e, size)
    ) {
      case (b, e, size) =>
        val r = Random.choose(b, e, size)
        r.forall { ele =>
          ele >= b && ele <= e
        }
    }
    assert(p.test(500))
  }
}
