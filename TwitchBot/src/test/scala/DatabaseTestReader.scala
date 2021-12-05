import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.Sink
import model.{WhiteListedDomain, WhiteListedDomainTable}
import slick.lifted
object DatabaseTestReader extends App {
  implicit val session: SlickSession = SlickSession.forConfig("slick-postgres")

  val system = ActorSystem("q")

  implicit val mat: Materializer = Materializer(system)

  import slick.jdbc.PostgresProfile.api._

  val db = Database.forConfig("slick-postgres2")
  //setup db
//  db.run(
//    DBIO.seq(
//      lifted.TableQuery[WhiteListedDomainTable].schema.create
//    )
//  )
  //put
  val create = DBIO.seq(
    lifted.TableQuery[WhiteListedDomainTable] ++= Seq(
      WhiteListedDomain(1, "youtube.com")
    )
  )

  val k = WhiteListedDomain(1, "youtube.com")

  def delete(k: WhiteListedDomain) = DBIO.seq(
    lifted.TableQuery[WhiteListedDomainTable].delete
  )

  db.run(delete(k))

  val query = Slick
    .source(TableQuery[WhiteListedDomainTable].result)
    .map(x => println(x))
    .runWith(Sink.ignore)

}
