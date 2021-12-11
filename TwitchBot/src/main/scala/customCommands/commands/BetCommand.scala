package customCommands.commands

import common.TwitchMessage
import db.DataBaseIO
import model.{BetSession, BetSessionTable, Bettor, BettorTable}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class BetCommand(
    tm: TwitchMessage,
    outcome: String,
    amount: Long,
    errors: Option[String]
) extends WithTwitchOutput {

  override def outputCommands: List[String] = {
    errors
      .map(error => List(s"PRIVMSG #sadbruh1 :/w ${tm.nickname.get} $error"))
      .getOrElse(
        List(
          s"PRIVMSG #sadbruh1 :/w ${tm.nickname.get} You have put $amount points on $outcome"
        )
      )
  }
}

object BetCommand extends CustomCommand[BetCommand] {
  override val signature = "bet"

  def updateSession(
      betSession: BetSession,
      bet: Long,
      outcome: String
  ): Future[Int] = {
    DataBaseIO.updateEntity[BetSession, BetSessionTable](
      TableQuery[BetSessionTable]
        .filter(en => en.state === "ongoing")
        .update(betSession.withUpdatedPool(bet, outcome))
    )
  }

  override def apply(
      tm: TwitchMessage,
      m: List[String]
  ): Future[Option[BetCommand]] = {

    val username = tm.nickname.get

    val bet = m.last.toLong

    val outcome = m.head match {
      case "win"  => "w"
      case "lose" => "l"
      case _      => "u"
    }

    val bettors =
      DataBaseIO.readEntities[Bettor, BettorTable](
        TableQuery[BettorTable].filter(en => en.nickname === username)
      )

    val betSession =
      DataBaseIO
        .readEntities[BetSession, BetSessionTable](
          TableQuery[BetSessionTable].filter(en => en.state === "ongoing")
        )
        .map(_.headOption)

    bettors
      .flatMap {
        case Nil => Future.successful(None)
        case bettors =>
          betSession.flatMap { session =>
            val bettor = bettors.head

            if (!bettor.validateBet(bet, outcome)) {
              Future.successful(
                Some(
                  BetCommand(
                    tm,
                    "",
                    0,
                    Some(s"Invalid bet command")
                  )
                )
              )
            } else if (!session.exists(_.ongoing)) {
              Future.successful(
                Some(
                  BetCommand(
                    tm,
                    "",
                    0,
                    Some(s"There are no active bets")
                  )
                )
              )
            } else {
              val update = DataBaseIO.updateEntity[Bettor, BettorTable](
                TableQuery[BettorTable]
                  .filter(en => en.nickname === username)
                  .update(bettor.makeBet(bet, outcome))
              )

              update
                .flatMap { _ =>
                  updateSession(session.get, bet, outcome)
                }
                .map(_ => Some(BetCommand(tm, m.head, bet, None)))
            }
          }
      }

  }
}
