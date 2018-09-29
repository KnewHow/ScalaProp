scalaVersion := "2.12.6"
name := "ScalaProp"
organization := "knewhow.me"
version := "1.0"
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.1.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
// scalafmtVersion in ThisBuild := "1.1.0"
// scalafmtOnCompile in ThisBuild := true
