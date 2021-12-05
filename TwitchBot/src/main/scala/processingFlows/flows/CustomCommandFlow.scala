package processingFlows.flows

import akka.stream.alpakka.amqp.{AmqpConnectionProvider, WriteMessage}
import akka.util.ByteString
import common.TwitchMessage
import customCommands.CustomCommands
import customCommands.commands.WithTwitchOutput
import processingFlows.common.ProcessingFlow

import scala.concurrent.Future

case class CustomCommandFlow(connectionProvider: AmqpConnectionProvider)
    extends ProcessingFlow[WithTwitchOutput] {

  override def isApplicableFor(m: TwitchMessage): Boolean = {
    val message = m.message.get
    val command = message.split(' ').toList.head.stripPrefix("!")

    message.startsWith("!") && CustomCommands
      .knownCommands()
      .map(_.signature)
      .contains(command)
  }

  override val queueName: String = "custom-command-queue"

  override def parseMessage(
      m: TwitchMessage
  ): Future[Option[WithTwitchOutput]] = {
    val message = m.message.get
    val command :: params = message.split(' ').toList

    Future.successful(
      CustomCommands
        .knownCommands()
        .find(_.signature == command.stripPrefix("!"))
        .flatMap(x => x.apply(params))
    )
  }

  override def fromMessageToCommands(
      c: WithTwitchOutput
  ): List[WriteMessage] =
    c.outputCommands().map(x => WriteMessage(ByteString(x)))
}
