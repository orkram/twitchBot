import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.Sink
import model.{
  Bettor,
  BettorTable,
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

  val k = Bettor(
    1,
    "dem_boy",
    1000,
    0,
    "u"
  )

  def delete(k: Bettor) = DBIO.seq(
    lifted
      .TableQuery[BettorTable]
      .filter(x => x.nickname === "aristol1738")
      .delete
  )

  db.run(delete(k))

  val query = Slick
    .source(TableQuery[FilteredTermTable].result)
    .runWith(Sink.ignore)

}
