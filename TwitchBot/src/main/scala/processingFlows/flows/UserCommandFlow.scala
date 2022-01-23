package processingFlows.flows

import akka.stream.alpakka.amqp.{AmqpConnectionProvider, WriteMessage}
import akka.util.ByteString
import common.TwitchMessage
import customCommands.commands.{SimpleOutputCommandResponse, WithTwitchOutput}
import db.DataBaseIO
import model.{UserCommand, UserCommandTable}
import processingFlows.common.ProcessingFlow
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UserCommandFlow(connectionProvider: AmqpConnectionProvider)
    extends ProcessingFlow[WithTwitchOutput] {

  private def knownUserCommands: Future[List[UserCommand]] = {
    DataBaseIO
      .readEntities[UserCommand, UserCommandTable](
        TableQuery[UserCommandTable]
      )
      .map(_.toList)
  }

  override def isApplicableFor(m: TwitchMessage): Boolean = {
    val message = m.message.get

    message.startsWith("!")
  }

  override val queueName: String = "command-queue"

  override def parseMessage(
      m: TwitchMessage
  ): Future[Option[WithTwitchOutput]] = {
    val message = m.message.get
    val signature :: params = message.split(' ').toList

    knownUserCommands
      .map { commands =>
        commands
          .find(c => c.signature == signature.dropWhile(_ == '!'))
          .map(
            _.getOutput(params)
              .map(s"@${m.nickname.get} " + _)
              .getOrElse(s"/w ${m.nickname.get} Wrong parameters")
          )
      }
      .map { c =>
        c.map(SimpleOutputCommandResponse(m, _))
      }
  }

  override def fromMessageToCommands(
      c: WithTwitchOutput
  ): List[WriteMessage] =
    c.outputCommands.map(x => WriteMessage(ByteString(x)))
}
