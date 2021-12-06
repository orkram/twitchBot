package model

import org.json4s.DefaultFormats
import slick.jdbc.PostgresProfile.api._

import java.time.OffsetDateTime
import scala.concurrent.duration.DurationInt

case class RecurringNotification(
    id: Long,
    notification: String,
    frequency: Long = 1.minutes.toSeconds, //in seconds
    lastExecuted: OffsetDateTime = OffsetDateTime.now()
) extends Entity {

  def readyToExecute: Boolean = {
    lastExecuted.plusSeconds(frequency).isBefore(OffsetDateTime.now())
  }

}

object RecurringNotification extends MarshallEntity[RecurringNotification] {
  def apply(
      id: Long,
      notification: String,
      frequency: Long,
      lastExecuted: OffsetDateTime
  ): RecurringNotification =
    new RecurringNotification(id, notification, frequency, lastExecuted)
}

class RecurringNotificationTable(tag: Tag)
    extends Table[RecurringNotification](tag, None, "RecurringNotification") {
  override def * =
    (id, notification, frequency, lastExecuted) <> (Function.tupled(
      RecurringNotification.apply
    ), RecurringNotification.unapply)

  implicit val formats: DefaultFormats = DefaultFormats

  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  val notification: Rep[String] = column[String]("notification")
  val frequency: Rep[Long] = column[Long]("frequency")
  val lastExecuted: Rep[OffsetDateTime] = column[OffsetDateTime]("lastExecuted")

}
