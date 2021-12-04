package model

import slick.jdbc.PostgresProfile.api._

case class WhiteListedDomain(
    id: Long,
    allowedDomain: String
)

object WhiteListedDomain extends MarshallEntity[WhiteListedDomain] {
  def create(id: Long, allowedDomain: String): WhiteListedDomain =
    WhiteListedDomain(id, allowedDomain)
}

class WhiteListedDomainTable(tag: Tag)
    extends Table[WhiteListedDomain](tag, None, "WhiteListedDomain") {
  override def * =
    (
      id,
      allowedDomain
    ) <> (Function.tupled(WhiteListedDomain.create), WhiteListedDomain.unapply)
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val allowedDomain: Rep[String] = column[String]("AllowedDomain")

}
