import Dependencies._

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.test.example"

resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"

lazy val root = (project in file("."))
  .settings(
    name := "zio-http",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-json" % "0.2.0-M2",
      "io.github.kitlangton" %% "zio-magic" % "0.3.10",
      "io.d11" %% "zhttp" % "1.0.0.0-RC17+47-0ea2e2b7-SNAPSHOT",
      scalaTest % Test
    )
  )
