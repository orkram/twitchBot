package model

import java.time.LocalDate
import slick.jdbc.PostgresProfile.api._

case class Player(
    id: Long,
    name: String,
    country: String,
    dob: Option[LocalDate]
)

class PlayerTable(tag: Tag) extends Table[Player](tag, None, "Player") {
  override def * = (id, name, country, dob) <> (Player.tupled, Player.unapply)
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val name: Rep[String] = column[String]("Name")
  val country: Rep[String] = column[String]("Country")
  val dob: Rep[Option[LocalDate]] = column[Option[LocalDate]]("Dob")
}
