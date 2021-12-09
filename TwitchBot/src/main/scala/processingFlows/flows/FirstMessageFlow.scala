package processingFlows.flows

import akka.stream.alpakka.amqp.{AmqpConnectionProvider, WriteMessage}
import akka.util.ByteString
import common.TwitchMessage
import customCommands.commands.{DeleteMessageCommand, WithTwitchOutput}
import db.DataBaseIO
import model.{Bettor, BettorTable}
import processingFlows.common.ProcessingFlow
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class FirstMessageFlow(connectionProvider: AmqpConnectionProvider)
    extends ProcessingFlow[WithTwitchOutput] {

  override def isApplicableFor(tm: TwitchMessage): Boolean =
    tm.nickname.nonEmpty

  override val queueName: String = "first-message-queue"

  override def parseMessage(
      tm: TwitchMessage
  ): Future[Option[WithTwitchOutput]] = {

    val bettors =
      DataBaseIO.readEntities[Bettor, BettorTable](
        TableQuery[BettorTable].filter(en => en.nickname === tm.nickname.get)
      )

    bettors
      .map {
        case Nil =>
          DataBaseIO.writeEntity[Bettor, BettorTable](
            TableQuery[BettorTable],
            Bettor(1, tm.nickname.get, 1000, 0, "u")
          )
          None
        case _ => None
      }

  }

  override def fromMessageToCommands(c: WithTwitchOutput): List[WriteMessage] =
    c.outputCommands.map(x => WriteMessage(ByteString(x)))
}
