package model

import slick.jdbc.PostgresProfile.api._

case class Bettor(
    id: Long,
    nickname: String,
    pool: Long,
    unsafePool: Long,
    outcome: String
) extends Entity {

  def makeBet(bet: Long, outcome: String): Bettor = {
    this.copy(unsafePool = bet, pool = pool - bet, outcome = outcome)
  }

  def finishBet(ratio: Double, result: String): Bettor = {
    if (result == outcome) {
      this.copy(
        pool = (unsafePool * ratio).toLong,
        unsafePool = 0,
        outcome = "u"
      )
    } else {
      this.copy(
        unsafePool = 0,
        outcome = "u"
      )
    }
  }
}

object Bettor extends MarshallEntity[Bettor] {
  def create(
      id: Long,
      nickname: String,
      pool: Long,
      unsafePool: Long,
      outcome: String
  ): Bettor =
    Bettor(id, nickname, pool, unsafePool, outcome)
}

class BettorTable(tag: Tag) extends Table[Bettor](tag, None, "Bettor") {
  override def * =
    (
      id,
      nickname,
      pool,
      unsafePool,
      outcome
    ) <> (Function.tupled(Bettor.create), Bettor.unapply)
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val nickname: Rep[String] = column[String]("nickname")
  val pool: Rep[Long] = column[Long]("pool")
  val unsafePool: Rep[Long] = column[Long]("unsafePool")
  val outcome: Rep[String] = column[String]("outcome")

}
