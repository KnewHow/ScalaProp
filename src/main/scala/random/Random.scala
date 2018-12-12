package prop.random

import prop.state.State

/**
  * A Random generate tool
  * @Author KnewHow
  * @Date 2018-12-12
  */
object Random {
  def choose(begin: Int, end: Int): Int =
    RNG.nextInt(begin, end + 1).run(RNG.get)._1
  def choose(begin: Int, end: Int, size: Int): Seq[Int] =
    State
      .sequence(List.fill(size)(RNG.nextInt(begin, end + 1)))
      .run(RNG.get)
      ._1
      .toSeq
}
