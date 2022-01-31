package api

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import api.config.ConfigurationEndpointsConfig
import common.ConfigLoader
import common.MessageLogger.logMessage
import configs.TwitchAmpqConfig

import scala.concurrent.{ExecutionContextExecutor, Future}

case class TwitchBotApi(ampqConfig: TwitchAmpqConfig) {

  implicit val system: ActorSystem[Nothing] =
    ActorSystem(Behaviors.empty, "api-system")

  implicit val executionContext: ExecutionContextExecutor =
    system.executionContext

  implicit val mat: Materializer = Materializer(system)

  val httpConfig: ConfigurationEndpointsConfig =
    ConfigLoader.loadConfig(classOf[ConfigurationEndpointsConfig])

  val routes: Route = ConfigurationRoutes(ampqConfig).baseRoute

  val runApi: Future[Http.ServerBinding] =
    Http()
      .newServerAt(httpConfig.uri, httpConfig.port)
      .bind(routes)
      .map { x =>
        logMessage(s"Server is up at ${httpConfig.uri}:${httpConfig.port}")
        x
      }
}
