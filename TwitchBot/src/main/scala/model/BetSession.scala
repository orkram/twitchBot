package model

import slick.jdbc.PostgresProfile.api._

case class BetSession(
    id: Long,
    state: String,
    winPool: Long,
    losePool: Long
) extends Entity {

  def ratio(outcome: String): Double = {
    if (outcome == "l") {
      1 + (winPool.toDouble / losePool.toDouble)
    } else {
      1 + (losePool.toDouble / winPool.toDouble)
    }
  }

  def ongoing: Boolean = state == "ongoing"

  def withUpdatedPool(bet: Long, outCome: String): BetSession = {
    if (outCome == "l") { this.copy(losePool = losePool + bet) }
    else if (outCome == "w") { this.copy(winPool = winPool + bet) }
    else this
  }
}

object BetSession extends MarshallEntity[BetSession] {
  def create(
      id: Long,
      state: String,
      winPool: Long,
      losePool: Long
  ): BetSession =
    BetSession(id, state, winPool, losePool)
}

class BetSessionTable(tag: Tag)
    extends Table[BetSession](tag, None, "BetSession") {
  override def * =
    (
      id,
      state,
      winPool,
      losePool
    ) <> (Function.tupled(BetSession.create), BetSession.unapply)
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val state: Rep[String] = column[String]("state")
  val winPool: Rep[Long] = column[Long]("winPool")
  val losePool: Rep[Long] = column[Long]("losePool")

}
