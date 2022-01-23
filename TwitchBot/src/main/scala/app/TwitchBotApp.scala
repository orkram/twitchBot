package app

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.amqp.AmqpUriConnectionProvider
import akka.stream.scaladsl.Sink
import api.TwitchBotApi
import com.typesafe.config.ConfigFactory
import common.ConfigLoader
import common.MessageLogger.{logMessage, logger}
import configs.TwitchWsConfig
import processingFlows.ProcessingFlows
import processingFlows.common.OnTickFlow
import twitchWebsocket.TwitchWebSocketConnection

import scala.concurrent.ExecutionContext.Implicits.global

object TwitchBotApp extends App {

  val config = ConfigFactory.load()

  val twitchWsConfig = ConfigLoader.loadConfig(classOf[TwitchWsConfig])

  val connectionProvider: AmqpUriConnectionProvider = AmqpUriConnectionProvider(
    "amqp://172.24.24.45:5672"
  )

  implicit val system: ActorSystem = ActorSystem("mySystem1")
  implicit val mat: Materializer = Materializer(system)

  val queues = ProcessingFlows.flows(connectionProvider).map(_.queueName)

  logger.info(connectionProvider.uri)

  val (upgradeResponse, promise) =
    TwitchWebSocketConnection(
      twitchWsConfig,
      connectionProvider,
      queues
    )
      .establishConnection()

  val processingFlows =
    ProcessingFlows
      .flows(connectionProvider)
      .map { graph =>
        logMessage("Staring graph: " + graph.queueName)
        graph.getGraph
          .runWith(Sink.last)
          .onComplete { value =>
            logMessage(s"Some stream completed successfully with $value")
          }

      }

  val tickFlow = OnTickFlow().source.run()

  val httpRoutesBinding = TwitchBotApi.runApi

}
