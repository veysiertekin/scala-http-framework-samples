lazy val akkaHttpVersion = "10.2.7"
lazy val akkaVersion = "2.6.17"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.test.http_example",
      scalaVersion := "2.13.4"
    )),
    name := "akka-http",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  )
