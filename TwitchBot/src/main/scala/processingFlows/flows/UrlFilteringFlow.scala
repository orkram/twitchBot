package processingFlows.flows

import akka.stream.alpakka.amqp.{AmqpConnectionProvider, WriteMessage}
import akka.util.ByteString
import common.MessageLogger.logMessage
import common.TwitchMessage
import customCommands.commands.{DeleteMessageCommand, WithTwitchOutput}
import processingFlows.common.ProcessingFlow

case class UrlFilteringFlow(connectionProvider: AmqpConnectionProvider)
    extends ProcessingFlow[WithTwitchOutput] {

  val urlMatcher =
    """^(http:/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$"""

  override def isApplicableFor(m: TwitchMessage): Boolean = {
    logMessage(s"testing url ${m.message.get.matches(urlMatcher)}")
    m.command.contains("PRIVMSG") && m.message.exists(_.matches(urlMatcher))
  }

  override val queueName: String = "url-filtering-queue"

  override def parseMessage(
      m: TwitchMessage
  ): Option[WithTwitchOutput] = {
    Some(DeleteMessageCommand(m))
  }

  override def fromMessageToCommands(c: WithTwitchOutput): List[WriteMessage] =
    c.outputCommands().map(x => WriteMessage(ByteString(x)))
}
