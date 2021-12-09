package model

import slick.jdbc.PostgresProfile.api._

case class BetSession(
    id: Long,
    state: String,
    winPool: Long,
    losePool: Long
) extends Entity {

  def ratio: Double = 1 + winPool / losePool
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
