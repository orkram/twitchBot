import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.{Sink, Source}
import model.{Player, PlayerTable}
import slick.lifted

import java.time.LocalDate

object DatabaseTestReader extends App {
  implicit val session: SlickSession = SlickSession.forConfig("slick-postgres")

  val system = ActorSystem("q")

  implicit val mat: Materializer = Materializer(system)

  import slick.jdbc.PostgresProfile.api._

  val db = Database.forConfig("slick-postgres2")
  //setup db
  db.run(
    DBIO.seq(
      lifted.TableQuery[PlayerTable].schema.create
    )
  )
  //put
  val create = DBIO.seq(
    lifted.TableQuery[PlayerTable] ++= Seq(
      Player(1, "name", "county", Some(LocalDate.now()))
    )
  )

  val query = Slick
    .source(TableQuery[PlayerTable].result)
    .runWith(Sink.seq)

}
