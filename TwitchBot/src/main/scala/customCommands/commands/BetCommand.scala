package customCommands.commands

import common.TwitchMessage
import db.DataBaseIO
import model.{Bettor, BettorTable}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class BetCommand(
    outcome: String,
    ammount: Long,
    errors: Option[String]
) extends WithTwitchOutput {

  override def outputCommands: List[String] = {
    errors
      .map(error => List(s"PRIVMSG #sadbruh1 :/w $error"))
      .getOrElse(
        List(s"PRIVMSG #sadbruh1 :/w You have bet $ammount on $outcome")
      )
  }
}

object BetCommand extends CustomCommand[BetCommand] {
  override val signature = "bet"

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

    bettors.flatMap {
      case Nil => Future.successful(None)
      case bettors =>
        val bettor = bettors.head
        if (bettor.validateBet(bet)) {
          val update = DataBaseIO.updateEntity[Bettor, BettorTable](
            TableQuery[BettorTable]
              .filter(en => en.nickname === username)
              .update(bettor.makeBet(bet, outcome))
          )
          update.map(_ => Some(BetCommand(m.head, bet, None)))
        } else {
          Future.successful(
            Some(BetCommand("", 0, Some("Invalid bet command")))
          )
        }
    }

  }
}
