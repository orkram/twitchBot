package model

import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case class RecurringNotification(
    id: Long,
    notification: String,
    frequency: FiniteDuration = (1.minutes)
)

object RecurringNotification extends MarshallEntity[Player] {
  def apply(
      id: Long,
      notification: String,
      frequency: FiniteDuration
  ): RecurringNotification =
    new RecurringNotification(id, notification, frequency)
}

class RecurringNotificationTable(tag: Tag)
    extends Table[RecurringNotification](tag, None, "RecurringNotification") {
  override def * =
    (id, notification, frequency) <> (Function.tupled(
      RecurringNotification.apply
    ), RecurringNotification.unapply)

  implicit val formats: DefaultFormats = DefaultFormats

  implicit def mapper: BaseColumnType[FiniteDuration] =
    MappedColumnType.base[FiniteDuration, String](
      time => Serialization.write[FiniteDuration](time),
      time => read[FiniteDuration](time)
    )

  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val notification: Rep[String] = column[String]("notification")
  val frequency: Rep[FiniteDuration] = column[FiniteDuration]("frequency")

}
