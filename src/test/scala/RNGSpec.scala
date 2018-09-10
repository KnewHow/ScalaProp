package test.prop

import org.scalatest.FlatSpec
import prop.stream._
import prop.gen._

class RNGSpec extends FlatSpec {
  "use scala test" should "success" in {
    val rng = RNG.get
    val a = RNG.nextInt(10, 20)
    val s = Stream.unfold(rng)(rng => Some(a.run(rng)))
    val r = s.take(10).toList
    println(s"list->$r")
    assert(true)
  }

  "use scala test1" should "success" in {
    val rng = RNG.get
    val a = RNG.nextInt(10, 20)
    val s = Stream.unfold(rng)(rng => Some(a.run(rng)))
    val r = s.take(10).toList
    println(s"list->$r")
    assert(true)
  }
}
