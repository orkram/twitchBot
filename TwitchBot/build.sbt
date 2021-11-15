name := "TwitchBot"

version := "0.1"

scalaVersion := "2.13.1"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime, // why this logger required,
  "org.json4s" %% "json4s-jackson" % "4.0.0"
)
