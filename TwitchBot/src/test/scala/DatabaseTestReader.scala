import akka.actor.ActorSystem
import akka.stream.Materializer
import model.{Player, PlayerTable}
import slick.lifted

import java.time.LocalDate

object DatabaseTestReader extends App {

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
  db.run(
    DBIO.seq(
      lifted.TableQuery[PlayerTable] ++= Seq(
        Player(1, "name", "county", Some(LocalDate.now()))
      )
    )
  )
  //
}
