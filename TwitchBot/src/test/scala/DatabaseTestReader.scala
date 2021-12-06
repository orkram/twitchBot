import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.Sink
import model.{
  RecurringNotification,
  RecurringNotificationTable,
  WhiteListedDomain,
  WhiteListedDomainTable
}
import slick.lifted

import java.time.OffsetDateTime
import scala.concurrent.duration.DurationInt
object DatabaseTestReader extends App {
  implicit val session: SlickSession = SlickSession.forConfig("slick-postgres")

  val system = ActorSystem("q")

  implicit val mat: Materializer = Materializer(system)

  import slick.jdbc.PostgresProfile.api._

  val db = Database.forConfig("slick-postgres2")
  //setup db
  db.run(
    DBIO.seq(
      lifted.TableQuery[RecurringNotificationTable].schema.create
    )
  )
  //put
  def create(k: RecurringNotification) = DBIO.seq(
    lifted.TableQuery[RecurringNotificationTable] ++= Seq(
      k
    )
  )

  val k = RecurringNotification(
    2,
    "youtube safe",
    5.second.toSeconds,
    OffsetDateTime.now()
  )

  def delete(k: WhiteListedDomain) = DBIO.seq(
    lifted.TableQuery[WhiteListedDomainTable].delete
  )

  db.run(create(k))

  val query = Slick
    .source(TableQuery[RecurringNotificationTable].result)
    .map(x => println(x.frequency))
    .runWith(Sink.ignore)

}
