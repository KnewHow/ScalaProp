package prop.random

import prop.state.State

/**
  * A class generate rand number, but it is need a seed first.
  * Most of its functions will return a `State`, which present a state to next state.
  * You can get detail from ` prop.state.State`
  * @Author KnewHow 2018-09-23
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

  def increasingInt(step: Long): State[RNG, Long] =
    for {
      r <- State.get
      _ <- State.set(r.copy(seed = r.seed + step))
    } yield r.seed

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

  def increasingInt(start: Long, step: Long): State[RNG, Long] =
    RNG(start).increasingInt(step)

  def unit[A](a: => A): State[RNG, A] = State.unit(a)

  def double = get.double

  def Simple(l: Long) = State.unit(l)

  def get: RNG = RNG(System.nanoTime)
}
