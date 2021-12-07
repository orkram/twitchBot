import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.Sink
import model.{
  FilteredTerm,
  FilteredTermTable,
  RecurringNotification,
  RecurringNotificationTable,
  UserCommand,
  UserCommandTable,
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
  def create(k: UserCommand) = DBIO.seq(
    lifted.TableQuery[UserCommandTable] ++= Seq(
      k
    )
  )

  val k = UserCommand(
    1,
    "subtime",
    "Have been subscribed to $1 for $2 months in a row. What a $3"
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
