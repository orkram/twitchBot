package customCommands.commands

import common.TwitchMessage
import db.DataBaseIO
import model.{Bettor, BettorTable}
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class CheckPointsCommand(tm: TwitchMessage, points: Long)
    extends WithTwitchOutput {

  override def outputCommands: List[String] = List(
    s"PRIVMSG #sadbruh1 :/w ${tm.nickname.get} You have $points points"
  )
}

object CheckPointsCommand extends CustomCommand[CheckPointsCommand] {
  override val signature = "points"

  override def apply(
      tm: TwitchMessage,
      m: List[String]
  ): Future[Some[CheckPointsCommand]] = {
    val points: Future[Long] = DataBaseIO
      .readEntities[Bettor, BettorTable](
        TableQuery[BettorTable]
          .filter(en => en.nickname === tm.nickname.get)
      )
      .map(bettors =>
        bettors.headOption.map(b => b.pool + b.unsafePool).getOrElse(0)
      )

    points.map(x => Some(CheckPointsCommand(tm, x)))
  }
}
