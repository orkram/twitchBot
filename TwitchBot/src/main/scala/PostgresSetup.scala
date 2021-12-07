import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.SlickSession
import model.DatabaseTablesEntities
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global

object PostgresSetup extends App {

  val db = Database.forConfig("slick-postgres2")

  implicit val session: SlickSession = SlickSession.forConfig("slick-postgres")

  val system = ActorSystem("setup")

  implicit val mat: Materializer = Materializer(system)

  DatabaseTablesEntities.tableDefinisions.foreach {
    db.run(
      _
    ).onComplete(_ => system.terminate())
  }
}
