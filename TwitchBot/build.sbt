name := "TwitchBot"

version := "latest"

scalaVersion := "2.13.1"

val AkkaVersion = "2.6.14"
val AkkaHttpVersion = "10.2.7"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime, // why this logger required,
  "org.json4s" %% "json4s-jackson" % "4.0.0",
  "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "3.0.3",
  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "3.0.3",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "org.postgresql" % "postgresql" % "42.2.5",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value
)

Compile / mainClass := Some("app.TwitchBotApp")

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(UniversalPlugin)
  .enablePlugins(DockerPlugin)
  .enablePlugins(DockerSpotifyClientPlugin)
  .settings(
    name := "bot-processor",
    dockerExposedPorts := Seq(8080)
  )
