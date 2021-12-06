package model

import slick.jdbc.PostgresProfile.api._

case class FilteredTerm(
    id: Long,
    term: String
) extends Entity

object FilteredTerm extends MarshallEntity[FilteredTerm] {
  def create(id: Long, allowedDomain: String): FilteredTerm =
    FilteredTerm(id, allowedDomain)
}

class FilteredTermTable(tag: Tag)
    extends Table[FilteredTerm](tag, None, "FilteredTerm") {
  override def * =
    (
      id,
      term
    ) <> (Function.tupled(FilteredTerm.create), FilteredTerm.unapply)
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val term: Rep[String] = column[String]("term")

}
