package app

import akka.actor.ActorSystem
import akka.stream.Supervision.Stop
import akka.stream.alpakka.amqp.AmqpUriConnectionProvider
import akka.stream.scaladsl.{RestartSource, Sink, Source}
import akka.stream.{ActorAttributes, Materializer, RestartSettings, Supervision}
import api.TwitchBotApi
import com.typesafe.config.ConfigFactory
import common.ConfigLoader
import common.MessageLogger.{logMessage, logger}
import configs.{TwitchAmpqConfig, TwitchWsConfig}
import processingFlows.ProcessingFlows
import processingFlows.common.OnTickFlow
import twitchWebsocket.TwitchWebSocketConnection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

object TwitchBotApp extends App {

  val config = ConfigFactory.load()

  val twitchWsConfig = ConfigLoader.loadConfig(classOf[TwitchWsConfig])

  val ampqConfig = ConfigLoader.loadConfig(classOf[TwitchAmpqConfig])

  val connectionProvider: AmqpUriConnectionProvider = AmqpUriConnectionProvider(
    ampqConfig.url
  )

  implicit val system: ActorSystem = ActorSystem("mySystem1")
  implicit val mat: Materializer = Materializer(system)

  val queues = ProcessingFlows.flows(connectionProvider).map(_.queueName)

  logger.info(connectionProvider.uri)

  val twitchWebSocketConnection = {
    Source(List(1))
      .map { _ =>
        TwitchWebSocketConnection(
          twitchWsConfig,
          connectionProvider,
          queues
        )
          .establishConnection()
      }
  }

  private val restartConfig = RestartSettings(
    5.seconds,
    1.minutes,
    0.2
  )
  private val supervisorStrategy: Supervision.Decider = { e =>
    logMessage(s"Flow has failed dramatically with exception: $e")
    Stop
  }

  Thread.sleep(
    5000
  ) // delay startup cause rabbitmq might not be ready when docker thinks it is, and restart strategy for connection doesnt fix the problem
  val webSocketConnection = RestartSource
    .onFailuresWithBackoff(restartConfig) { () =>
      logMessage(s"Starting flow " + this.getClass.getSimpleName)
      twitchWebSocketConnection.withAttributes(
        ActorAttributes.supervisionStrategy(supervisorStrategy)
      )
    }
    .run()

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

  val tickFlow = OnTickFlow(ampqConfig).source.run()

  val httpRoutesBinding = TwitchBotApi.runApi

}
