package processingFlows.flows

import akka.stream.alpakka.amqp.{AmqpConnectionProvider, WriteMessage}
import akka.util.ByteString
import common.TwitchMessage
import customCommands.commands.{PongMessageOutput, WithTwitchOutput}
import processingFlows.common.ProcessingFlow

import scala.concurrent.Future

case class PongFlow(connectionProvider: AmqpConnectionProvider)
    extends ProcessingFlow[WithTwitchOutput] {

  override def isApplicableFor(tm: TwitchMessage): Boolean =
    tm.nickname.isEmpty && tm.channel.isEmpty && tm.message.contains(
      "tmi.twitch.tv"
    )

  override val queueName: String = "pong-queue"

  override def parseMessage(
      tm: TwitchMessage
  ): Future[Option[WithTwitchOutput]] = {

    Future.successful(Some(PongMessageOutput(tm)))
  }

  override def fromMessageToCommands(c: WithTwitchOutput): List[WriteMessage] =
    c.outputCommands.map(x => WriteMessage(ByteString(x)))
}
