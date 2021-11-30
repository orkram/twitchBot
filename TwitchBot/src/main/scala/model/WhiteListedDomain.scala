package model

import slick.jdbc.PostgresProfile.api._

case class WhiteListedDomain(
    id: Long,
    allowedDomain: String
)

class WhiteListedDomainTable(tag: Tag)
    extends Table[WhiteListedDomain](tag, None, "WhiteListedDomain") {
  override def * =
    (id, allowedDomain) <> (WhiteListedDomain.tupled, WhiteListedDomain.unapply)
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val allowedDomain: Rep[String] = column[String]("AllowedDomain")

}
