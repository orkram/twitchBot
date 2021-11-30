package db

import akka.Done
import akka.actor.ActorSystem
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.{Sink, Source}
import model.PlayerTable
import slick.jdbc.GetResult
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
@Deprecated
case class DataBaseIO() extends DbConnection {

  implicit val system: ActorSystem = ActorSystem("dataBaseActor")
  system.registerOnTermination(
    session.close()
  ) // todo session still probably wont terminate properly, move actor system somewhere else?

  //implicit val mat: Materializer = Materializer(system) //todo to be removed,

  def dbName(entity: DbTemplate): String =
    s"Twitch_${entity.getClass.getSimpleName.stripSuffix("$")}s"

  private def parseVariables(variables: DbEntity): String = {
    variables.productIterator.toList
      .mkString(", ")
  }
  def readEntity[A <: DbEntity](id: Int, a: DbTemplate)(implicit
      getResult: GetResult[A]
  ): Future[Option[A]] =
    Slick
      .source(
        sql"""SELECT #${a.fields} FROM #${dbName(a)} WHERE id=#$id""".as[A]
      )
      .runWith(Sink.seq[A])
      .map(_.headOption)

  def writeEntity[A <: DbEntity](
      variables: DbEntity,
      a: DbTemplate
  )(implicit getResult: GetResult[A]): Future[Done] =
    Source(0 to 1)
      .via(
        Slick.flow(_ =>
          sqlu"""INSERT INTO #${dbName(a)} VALUES(#${parseVariables(
            variables
          )});"""
        )
      )
      .runWith(Sink.ignore)

  def readAllEntities[A <: DbEntity](
      a: DbTemplate
  )(implicit getResult: GetResult[A]): Future[Seq[A]] = Slick
    .source(
      sql"""SELECT #${a.fields} FROM #${dbName(a)}""".as[A]
    )
    .runWith(Sink.seq[A])

}
