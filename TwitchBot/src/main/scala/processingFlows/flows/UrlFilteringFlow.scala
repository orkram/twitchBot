package processingFlows.flows

import akka.stream.alpakka.amqp.{AmqpConnectionProvider, WriteMessage}
import akka.util.ByteString
import common.MessageLogger.logMessage
import common.TwitchMessage
import customCommands.commands.{DeleteMessageCommand, WithTwitchOutput}
import db.DataBaseIO
import model.{WhiteListedDomain, WhiteListedDomainTable}
import processingFlows.common.ProcessingFlow
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
  ): Future[Option[WithTwitchOutput]] = {

    val whiteLists =
      DataBaseIO.readEntities[WhiteListedDomain, WhiteListedDomainTable](
        TableQuery[WhiteListedDomainTable]
      )

    whiteLists
      .map { wl =>
        wl.exists(x => m.message.exists(_.contains(x.allowedDomain)))
      }
      .map {
        case true  => None
        case false => Some(DeleteMessageCommand(m))
      }
  }

  override def fromMessageToCommands(c: WithTwitchOutput): List[WriteMessage] =
    c.outputCommands().map(x => WriteMessage(ByteString(x)))
}
