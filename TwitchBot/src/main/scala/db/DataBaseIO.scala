package db

import akka.actor.ActorSystem
import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.scaladsl.Sink
import slick.jdbc.PostgresProfile.api._
import slick.lifted.AbstractTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class DataBaseIO() extends DbConnection {

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

  def readEntity[A, Table <: AbstractTable[A]]
      : Future[Seq[Table#TableElementType]] = ???

}
