val tapirVersion = "0.19.0"
val akkaHttpVersion = "10.2.7"
val akkaVersion = "2.6.17"

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    inThisBuild(List(
      organization := "com.test.http_example",
      scalaVersion := "2.13.4"
    )),
    name := "akka-http",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-redoc-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion
    ),
    buildInfoPackage := "com.test.http_example"
  )
