package test.prop.gen

import org.scalatest.FlatSpec
import prop.gen._

class StringNSpec extends FlatSpec {
  "test StringN function" should "success" in {
    val s = Gen.stringN(10)
    val l = Gen.run(s).take(10).toList
    println(l)
    l.sorted
    l.map(r => assert(r.length == 10))
  }
  "test toChar function" should "success" in {
    val r = 0 to 127
    val rs = r.map(_.toChar)
    // println(rs)
  }

}
