package prop.gen

import prop.state.State

/**
  * A class generate rand number, but it is need a seed first.
  * Most of its functions will return a `State`, which present a state to next state.
  * You can get detail from ` prop.state.State`
  * @Author How
  */
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

  /**
    * produce a int with range
    */
  def nextInt(start: Int, stopExclusive: Int): State[RNG, Int] =
    nonNegativeInt.map { r =>
      start + r % (stopExclusive - start)
    }

  def double: State[RNG, Double] = nextInt.map(r => r / (Int.MaxValue))

}

object RNG {

  /**
    * use current millisecond as seed to make sure get differen `rng` when you call each time
    */
  def boolean: State[RNG, Boolean] = get.boolean
  def nonNegativeInt: State[RNG, Int] = get.nonNegativeInt
  def nextInt(start: Int, stopExclusive: Int): State[RNG, Int] =
    get.nextInt(start, stopExclusive)

  def double = get.double

  def Simple(l: Long) = State.unit(l)

  def get: RNG = RNG(System.nanoTime)
}
