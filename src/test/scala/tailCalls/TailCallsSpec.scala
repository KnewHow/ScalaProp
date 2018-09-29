package test.prop.tailcalls

import org.scalatest.FlatSpec
import scala.util.control.TailCalls._

class TailCallsSpec extends FlatSpec {
  val n = 40
  val stdR = 102334155
  def fib(n: Int): Int = if (n < 2) n else fib(n - 1) + fib(n - 2)

  def fibWithTailCalls(n: Int): TailRec[Int] =
    if (n < 2) done(n)
    else
      for {
        x <- tailcall(fibWithTailCalls(n - 1))
        y <- tailcall(fibWithTailCalls(n - 2))
      } yield (x + y)

  // "test fib with original function" should "success" in {
  //   val r = fib(n)
  //   assert(r == stdR)
  // }

  "test fin with tail call" should "success" in {
    val r = fibWithTailCalls(n)
    assert(r.result == stdR)
  }
}
