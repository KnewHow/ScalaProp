# scala-test-prop  Scala Test Case Generator And Exector

Scala-tes-prop provides test cases generator then run test function with them. Finally it will println test result information and return a boolean value to tell you test result which can be asserted by scala test.

## Generator
Generator is basic component in scala-test-prop, which can generate test cases you want. For example, if you want to get a range integer, you see following code:
```Scala
import prop.gen.Gen

// return a Gen[Int] with integer range in (1,10), but the range exclusive 10
Gen.choose(1, 10)
```
It also can generate other generator, you can find them in [Gen]()
## Executor

## Incremental Test Case

## Bugs And

## How to Get
