package api

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import api.config.ConfigurationEndpointsConfig
import common.ConfigLoader
import common.MessageLogger.logMessage

import scala.concurrent.ExecutionContextExecutor

object TwitchBotApi {

  implicit val system: ActorSystem[Nothing] =
    ActorSystem(Behaviors.empty, "api-system")

  implicit val executionContext: ExecutionContextExecutor =
    system.executionContext

  val httpConfig: ConfigurationEndpointsConfig =
    ConfigLoader.loadConfig(classOf[ConfigurationEndpointsConfig])

  val routes: Route = ConfigurationRoutes().baseRoute

  val runApi =
    Http()
      .newServerAt(httpConfig.uri, httpConfig.port)
      .bind(ConfigurationRoutes().baseRoute)
      .map { x =>
        logMessage(s"Server is up at ${httpConfig.uri}:${httpConfig.port}")
        x
      }
}
