package processingFlows.common

import akka.stream.Supervision.Stop
import akka.stream.alpakka.amqp.{
  AmqpConnectionProvider,
  WriteMessage,
  WriteResult
}
import akka.stream.scaladsl.{Flow, RestartSource, Source}
import akka.stream.{ActorAttributes, RestartSettings, Supervision}
import akka.util.ByteString
import akka.{Done, NotUsed}
import common.MessageLogger.logMessage
import common.TwitchMessage
import customCommands.commands.WithTwitchOutput
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.read
import rabbitmq.{RmqMessageReaderFlow, RmqMessageWriterFlow}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

trait ProcessingFlow[O <: WithTwitchOutput] {

  def isApplicableFor(m: TwitchMessage): Boolean

  val queueName: String //rabbitMq queue where messages are being read

  implicit val formats: DefaultFormats = DefaultFormats

  def parseMessage(m: TwitchMessage): Future[Option[O]]

  val connectionProvider: AmqpConnectionProvider

  private val toRmq: Flow[WriteMessage, WriteResult, Future[Done]] =
    RmqMessageWriterFlow(
      "twitch-command-queue",
      connectionProvider
    ).amqpFlow

  private def fromRmq: Source[TwitchMessage, NotUsed] =
    RmqMessageReaderFlow(queueName, connectionProvider).amqpSource
      .map(s => s.bytes.decodeString(ByteString.UTF_8))
      .map { s =>
        read[TwitchMessage](s)
      }

  private val supervisorStrategy: Supervision.Decider = { e =>
    logMessage(s"Flow has failed dramatically with exception $e")
    Stop
  }

  private def process: Flow[TwitchMessage, WriteResult, NotUsed] = {
    Flow[TwitchMessage]
      .mapAsync(1) {
        case x if isApplicableFor(x) =>
          logMessage(queueName + ": " + x)
          parseMessage(x)
        case _ =>
          Future(None) // if this case isn't handled, flow completes
      }
      .recover { case e =>
        throw e
      }
      .mapConcat {
        case Some(message) => fromMessageToCommands(message)
        case None          => Nil
      }
      .via(toRmq)
      .withAttributes(ActorAttributes.supervisionStrategy(supervisorStrategy))
  }

  def fromMessageToCommands(c: O): List[WriteMessage]

  private val restartConfig = RestartSettings(
    10.seconds,
    2.minutes,
    0.2
  )
  def getGraph: Source[WriteResult, NotUsed] = {
    RestartSource.onFailuresWithBackoff(restartConfig) { () =>
      logMessage(s"Restarted flow")
      fromRmq
        .via(process)
    }
  }
}
