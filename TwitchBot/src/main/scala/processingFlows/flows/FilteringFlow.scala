package processingFlows.flows

import akka.stream.alpakka.amqp.{AmqpConnectionProvider, WriteMessage}
import akka.util.ByteString
import common.TwitchMessage
import customCommands.commands.{
  DeleteMessageCommand,
  TimeoutCommand,
  WithTwitchOutput
}
import db.DataBaseIO
import model.{
  FilteredTerm,
  FilteredTermTable,
  WhiteListedDomain,
  WhiteListedDomainTable
}
import processingFlows.common.ProcessingFlow
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class FilteringFlow(connectionProvider: AmqpConnectionProvider)
    extends ProcessingFlow[WithTwitchOutput] {

  override def isApplicableFor(m: TwitchMessage): Boolean = true

  override val queueName: String = "message-filtering-queue"

  override def parseMessage(
      m: TwitchMessage
  ): Future[Option[WithTwitchOutput]] = {

    val filteredTerms =
      DataBaseIO.readEntities[FilteredTerm, FilteredTermTable](
        TableQuery[FilteredTermTable]
      )

    filteredTerms
      .map { wl =>
        wl.exists(x => m.message.exists(_.contains(x.term)))
      }
      .map {
        case true  => Some(DeleteMessageCommand(m))
        case false => None
      }
  }

  override def fromMessageToCommands(c: WithTwitchOutput): List[WriteMessage] =
    c.outputCommands.map(x => WriteMessage(ByteString(x)))
}
