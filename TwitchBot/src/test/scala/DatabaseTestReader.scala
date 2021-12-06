import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.Sink
import model.{
  FilteredTerm,
  FilteredTermTable,
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
//  db.run(
//    DBIO.seq(
//      lifted.TableQuery[FilteredTermTable].schema.create
//    )
//  )
  //put
  def create(k: FilteredTerm) = DBIO.seq(
    lifted.TableQuery[FilteredTermTable] ++= Seq(
      k
    )
  )

  val k = FilteredTerm(
    2,
    "BatChest"
  )
//
//  def delete(k: WhiteListedDomain) = DBIO.seq(
//    lifted.TableQuery[WhiteListedDomainTable].delete
//  )
//
  db.run(create(k))

  val query = Slick
    .source(TableQuery[FilteredTermTable].result)
    .runWith(Sink.ignore)

}
