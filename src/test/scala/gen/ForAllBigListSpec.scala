package test.prop.gen
import org.scalatest.FlatSpec
import prop.gen._

class ForAllBigListSpec extends FlatSpec {
  val g = Gen.listOfN(Gen.choose(1000000, 5000000), Gen.choose(5000, 10000))
  val r = Prop.forAll(g) { r =>
    val s = r.sorted
    val min = s.headOption
    val re = min.map(m => !s.exists(_ < m))
    re.getOrElse(true)
  }
  assert(r.test())
}
