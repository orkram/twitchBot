package model

import slick.jdbc.PostgresProfile.api._

case class UserCommand(
    id: Long,
    signature: String,
    output: String
) extends Entity {

  def fixOutput(output: String, params: List[String]): String = {
    if (params.isEmpty) {
      output
    } else {
      val newOutput = output.replaceFirst("""\$[0-9]""", params.last)
      fixOutput(newOutput, params.dropRight(1))
    }
  }

  def getOutput(params: List[String]): Option[String] = {

    if (output.count(c => c == '$') == params.length) {
      Some(fixOutput(output, params.reverse))
    } else {
      None
    }
  }
}

object UserCommand extends MarshallEntity[UserCommand] {
  def create(
      id: Long,
      signature: String,
      output: String
  ): UserCommand =
    UserCommand(id, signature, output)
}

class UserCommandTable(tag: Tag)
    extends Table[UserCommand](tag, None, "UserCommand") {

//  implicit def mapper: BaseColumnType[List[String]] =
//    MappedColumnType.base[List[String], String](
//      params => params.mkString(";"),
//      params => params.split(";").toList
//    )

  override def * =
    (
      id,
      signature,
      output
    ) <> (Function.tupled(UserCommand.create), UserCommand.unapply)
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val signature: Rep[String] = column[String]("signature")
  val output: Rep[String] = column[String]("output")
}
