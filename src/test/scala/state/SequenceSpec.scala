package test.prop.state

import prop.state.State
import prop.random.RNG
import prop.gen._
import prop.stream.Stream
import org.scalatest.FlatSpec

class SequencSpec extends FlatSpec {
  val s = RNG.nextInt(1, 10)
  val sL = State.sequence(List.fill(10000)(s))
  val rng = RNG(42)
  val r = Stream.unfold(rng)(rng => Some(sL.run(rng)))
  r.take(1).toList
}
