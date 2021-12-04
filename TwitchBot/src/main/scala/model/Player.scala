package model

import java.time.LocalDate
import slick.jdbc.PostgresProfile.api._

case class Player(
    id: Long,
    name: String,
    country: String,
    dob: Option[LocalDate]
)

object Player extends MarshallEntity[Player] {
  def apply(
      id: Long,
      name: String,
      country: String,
      dod: Option[LocalDate]
  ): Player =
    new Player(id, name, country, dod)

}

class PlayerTable(tag: Tag) extends Table[Player](tag, None, "Player") {
  override def * =
    (id, name, country, dob) <> (Function.tupled(Player.apply), Player.unapply)
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val name: Rep[String] = column[String]("Name")
  val country: Rep[String] = column[String]("Country")
  val dob: Rep[Option[LocalDate]] = column[Option[LocalDate]]("Dob")
}
