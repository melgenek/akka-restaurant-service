name := "akka-restaurant-service"

version := "0.1"

scalaVersion := "2.12.6"

val Versions = new {
  val akkaHttp = "10.1.3"
  val circe = "0.9.3"
  val slick = "3.2.3"
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp,

  "de.heikoseeberger" %% "akka-http-circe" % "1.21.0",
  "io.circe" %% "circe-core" % Versions.circe,
  "io.circe" %% "circe-generic" % Versions.circe,
  "io.circe" %% "circe-parser" % Versions.circe,

  "io.scalaland" %% "chimney" % "0.2.0",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.4.0",

  "org.slf4j" % "slf4j-api" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",

  "org.scalatest" %% "scalatest" % "3.0.5" % "test,it",
  "org.mockito" % "mockito-core" % "1.10.19" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp % "test,it",
  "com.dimafeng" %% "testcontainers-scala" % "0.19.0" % "it"

)

configs(IntegrationTest)
Defaults.itSettings
