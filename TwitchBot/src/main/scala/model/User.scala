package model

import db.{DbEntity, DbTemplate}
import slick.jdbc.GetResult
@Deprecated
case class User(
    id: Int,
    name: String
) extends DbEntity

object User extends DbTemplate {

  val fields: String = fieldsFromParams[User]

  implicit val getUserResult: AnyRef with GetResult[User] =
    GetResult(r => User(r.nextInt, r.nextString))

}
