package db

import akka.actor.ActorSystem
import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.scaladsl.Sink
import model.WhiteListedDomainTable
import slick.jdbc.PostgresProfile.api._
import slick.lifted
import slick.lifted.{AbstractTable, TableQuery}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DataBaseIO extends DbConnection {

  implicit val system: ActorSystem = ActorSystem("dataBaseActor")

  val db = Database.forConfig("slick-postgres2")

  system.registerOnTermination(
    session.close()
  ) // todo session still probably wont terminate properly, move actor system somewhere else?

  def readEntities[E, T <: Table[E]](
      tableQuery: TableQuery[T]
  ): Future[Seq[E]] =
    Slick
      .source(tableQuery.result)
      .runWith(Sink.seq)

  def writeEntity[A, Table <: AbstractTable[A]](
      tableQuery: TableQuery[Table],
      a: Table#TableElementType
  ): Future[Table#TableElementType] = {
    db.run(
      DBIO.seq(
        tableQuery ++= Seq(a)
      )
    ).map(_ => a)
  }

  def removeEntity[T <: AbstractTable[A], A](
      tableQuery: DBIOAction[Int, NoStream, Effect.Write]
  ): Future[Int] = {
    db.run(
      tableQuery
    )
  }

}
