package test.prop.gen
import org.scalatest.FlatSpec
import prop.gen._

class ListOfFixedSizeSpec extends FlatSpec {
  "test listOf random" should "success" in {
    val g = Gen.listOfN(10, Gen.choose(10, 100))
    val p = Prop.forAll(g) { r =>
      val s = r.sorted
      val h = s.headOption
      h.map(rs => !r.exists(_ < rs)).getOrElse(true)
    }
    assert(p.test())
  }
}
