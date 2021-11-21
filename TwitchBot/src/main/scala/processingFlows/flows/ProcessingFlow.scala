package processingFlows.flows

import akka.stream.alpakka.amqp.{WriteMessage, WriteResult}
import akka.stream.scaladsl.{Flow, Source}
import akka.{Done, NotUsed}
import common.TwitchMessage
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.read
import rabbitmq.{RmqMessageReaderFlow, RmqMessageWriterFlow}

import scala.concurrent.Future

trait ProcessingFlow[O <: Nothing] {

  def isApplicableFor(m: TwitchMessage): Boolean

  val queueName: String //rabbitMq queue where messages are being read

  implicit val formats: DefaultFormats = DefaultFormats

  def parseMessage(m: TwitchMessage): O

  val toRmq: Flow[WriteMessage, WriteResult, Future[Done]] =
    RmqMessageWriterFlow(
      "twitch-command-queue"
    ).amqpFlow

  val fromRmq: Source[TwitchMessage, NotUsed] =
    RmqMessageReaderFlow(queueName).amqpSource.map(s =>
      read[TwitchMessage](s.toString)
    )

  def process(): Flow[TwitchMessage, WriteResult, NotUsed] = {
    Flow[TwitchMessage]
      //also potentially writes to database here? maybe
      .mapConcat(fromMessageToCommands)
      .via(toRmq)
  }

  def fromMessageToCommands(m: TwitchMessage): List[WriteMessage]

}
