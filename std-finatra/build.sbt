

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.test.example"

lazy val root = (project in file("."))
  .settings(
    name := "finatra",
    libraryDependencies ++= Seq(
      "com.twitter" %% "finatra-http-server" % "21.10.0"
    )
  )
