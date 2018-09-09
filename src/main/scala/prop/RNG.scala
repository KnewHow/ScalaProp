package fpscala.testing

import prop.state.State

case class RNG(seed: Long) {
  def nextInt: State[RNG, Int] = {
    State[RNG, Int](
      r => {
        val newSeed = (r.seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
        val nextRNG = RNG(newSeed)
        val n = (newSeed >>> 16).toInt
        n -> nextRNG
      }
    )
  }

  def boolean: State[RNG, Boolean] = nextInt.map(r => r % 2 == 0)

  def nonNegativeInt: State[RNG, Int] =
    nextInt.map { r =>
      if (r < 0) {
        -r + 1
      } else {
        r
      }
    }
  def nextInt(start: Int, stopExclusive: Int): State[RNG, Int] =
    nonNegativeInt.map { r =>
      start + r % (stopExclusive - start)
    }

  def double: State[RNG, Double] = nextInt.map(r => r / (Int.MaxValue))

}

object RNG {
  val r = RNG(System.currentTimeMillis)
  def boolean: State[RNG, Boolean] = r.boolean
  def nonNegativeInt: State[RNG, Int] = r.nonNegativeInt
  def nextInt(start: Int, stopExclusive: Int): State[RNG, Int] =
    r.nextInt(start, stopExclusive)

  def double = r.double

  def Simple(l: Long) = State.unit(l)

  def get: RNG = r
}
