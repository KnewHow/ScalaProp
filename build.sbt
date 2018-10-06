scalaVersion := "2.12.6"
name := "ScalaProp"
organization := "www.knewhow.me.scala"
version := "1.0"
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.1.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2/")
}
// Add the default sonatype repository setting
publishTo := sonatypePublishTo.value

scalafmtVersion in ThisBuild := "1.1.0"
scalafmtOnCompile in ThisBuild := true
