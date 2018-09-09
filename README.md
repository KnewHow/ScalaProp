# Scala Check

## requirement

* ForAll[A](A => Boolean) function should return boolean result,  which can be asserted by scalaTest. And println some test information. If all test cases pass, println "The amount of the test cases passed", otherwise println the reasons for failure.

* Provide APIs to generate test datas. Such as `[A]`, `List[A]`,`odd`, `even` and so on. It would gives different results meet the requirements when call them each time. And you can choose differet range and size.

* But sometimes, we want use some test cases to test different function, For example, you hava two sort function, you want test which performance is better, first you may want to use same test cases to test them.  So we alse prodive some APIs generated some test cases when you call it each time.

* Beacause our check tool is functional style, test cases generation will be hiden, we will genreate in inner, But sometimes you want to obtain test cases, then tese them by yourself. So we will provide some APIs to return test cases directly.
