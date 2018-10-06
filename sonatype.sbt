// Your profile name of the sonatype account. The default is the same with the organization value
sonatypeProfileName := "com.github.knewhow"

// To sync with Maven central, you need to supply the following information:
publishMavenStyle := true

// License of your choice
licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

// Where is the source code hosted
import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(
  GitLabHosting("KnewHow", "ScalaProp", "948170910@qq.com"))

// or if you want to set these fields manually
homepage := Some(url("https://github.com/KnewHow/ScalaProp"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/KnewHow/ScalaProp"),
    "git@github.com:KnewHow/ScalaProp.git"
  )
)
developers := List(
  Developer(id = "KnewHow",
            name = "KnewHow",
            email = "948170910@qq.com",
            url = url("https://github.com/KnewHow"))
)
