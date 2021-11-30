package api

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import api.config.ConfigurationEndpointsConfig
import common.ConfigLoader
import common.MessageLogger.logMessage

import scala.concurrent.ExecutionContextExecutor

object ConfigurationApi extends App {

  implicit val system =
    ActorSystem(Behaviors.empty, "my-system")

  implicit val executionContext: ExecutionContextExecutor =
    system.executionContext

  val httpConfig =
    ConfigLoader.loadConfig(classOf[ConfigurationEndpointsConfig])

  val bindingFuture =
    Http()
      .newServerAt(httpConfig.uri, httpConfig.port)
      .bind(ConfigurationRoutes().baseRoute)
      .map { x =>
        logMessage(s"Server is up at ${httpConfig.uri}:${httpConfig.port}")
        x
      }
}
