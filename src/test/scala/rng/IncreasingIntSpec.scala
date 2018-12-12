package test.prop.rng
import prop.random.RNG
import prop.state.State
import org.scalatest.FlatSpec

class RNGIncreasingIntSpec extends FlatSpec {
  "test rng increasing" should "succeed" in {
    val s = RNG.increasingInt(10, 20)
    val r = State.sequence(List.fill(10)(s)).run(RNG(100))
    println(s"r->$r")
  }
}
