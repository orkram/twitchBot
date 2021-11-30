package processingFlows.flows

import akka.stream.alpakka.amqp.{
  AmqpConnectionProvider,
  WriteMessage,
  WriteResult
}
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}
import akka.util.ByteString
import akka.{Done, NotUsed}
import common.MessageLogger.logMessage
import common.TwitchMessage
import customCommands.commands.WithTwitchOutput
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.read
import rabbitmq.{RmqMessageReaderFlow, RmqMessageWriterFlow}

import scala.concurrent.Future

trait ProcessingFlow[O <: WithTwitchOutput] {

  def isApplicableFor(m: TwitchMessage): Boolean

  val queueName: String //rabbitMq queue where messages are being read

  implicit val formats: DefaultFormats = DefaultFormats

  def parseMessage(m: TwitchMessage): Option[O]

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
        logMessage(s)
        read[TwitchMessage](s)
      }

  private def process: Flow[TwitchMessage, WriteResult, NotUsed] = {
    Flow[TwitchMessage]
      //also potentially writes to database here? maybe
      .map {
        case x if isApplicableFor(x) =>
          logMessage("Recognized command: " + x.message)
          parseMessage(x)
        case _ => None // if this case isn't handled, flow completes
      }
      .mapConcat {
        case Some(message) => fromMessageToCommands(message)
        case None          => Nil
      }
      .via(toRmq)
  }

  def fromMessageToCommands(c: O): List[WriteMessage]

  def getGraph: RunnableGraph[NotUsed] = {
    fromRmq
      .via(process)
      .map(x => logMessage(x.toString()))
      .to(Sink.ignore)
  }
}
