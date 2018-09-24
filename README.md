# scala-test-prop  Scala Test Case Generator And Exector

Scala-tes-prop provides test cases generator then run test function with them. Finally it will println test result information and return a boolean value to tell you test result which can be asserted by scala test.

## Generator
Generator is basic component in scala-test-prop, which can generate test cases you want. For example, if you want to get a range integer, you see following code:
```Scala
import prop.gen.Gen

// return a Gen[Int] with integer range in (1,10), but the range exclusive 10
Gen.choose(1, 10)
```
It also can generate other generator, such as `Gen.odd(10,100)`, `Gen.listOf(10, Gen.choose(10,100))`. The first function will return random odd integer from 10 to 100, but inclusive 100. The second function will return a list with 10 size whose
elements range is between 10 and 100 but exclusive 100. You can find more in [Object Gen](https://github.com/KnewHow/ScalaProp/blob/master/src/main/scala/prop/Gen.scala)
## Executor

## Incremental Test Case

## Bugs And

## How to Get
