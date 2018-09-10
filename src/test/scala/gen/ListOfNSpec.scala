package test.prop.gen

import org.scalatest.FlatSpec
import prop.gen._
class LisnOfNSpec extends FlatSpec {
  "test listOfN with fixed size" should "success" in {
    val l = Gen.listOfN(10, Gen.choose(0, 10))
    val r = Gen.run(l).take(10).toList
    println(r.map(rs => rs.size))
    r.map(rs => assert(rs.size == 10 && !rs.exists(_ > 10)))
  }

  "test listOfN with variable size" should "success" in {
    val l = Gen.listOfN(Gen.choose(100, 300), Gen.choose(0, 10))
    Gen
      .run(l)
      .take(100)
      .toList
      .map(r => assert(r.size >= 100 && r.size <= 300 && !r.exists(_ > 10)))
  }
}
