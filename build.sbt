scalaVersion := "2.12.6"
name := "ScalaProp"
organization := "com.github.knewhow"
version := "1.0"
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.1.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
scalafmtVersion in ThisBuild := "1.1.0"
scalafmtOnCompile in ThisBuild := true

// PUBLISH SETTING
publishTo := sonatypePublishTo.value

ThisBuild / organization := "com.github.knewhow"
ThisBuild / organizationName := "scalaProp"
ThisBuild / organizationHomepage := Some(url("https://github.com/KnewHow"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/KnewHow/ScalaProp"),
    "scm:git@github.com:KnewHow/ScalaProp.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "KnewHow",
    name = "KnewHow",
    email = "how.yuangh@gmail.com",
    url = url("http://www.knewhow.me")
  )
)

ThisBuild / description := "A scala test prop and generator"
ThisBuild / licenses := List(
  "Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/KnewHow/ScalaProp"))
