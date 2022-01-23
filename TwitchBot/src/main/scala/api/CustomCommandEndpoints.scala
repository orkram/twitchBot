package api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import db.DataBaseIO
import model.{UserCommand, UserCommandTable}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

case class CustomCommandEndpoints() {

  def filteredTermsQueryResult: Future[List[UserCommand]] =
    DataBaseIO
      .readEntities[UserCommand, UserCommandTable](
        TableQuery[UserCommandTable]
      )
      .map(_.toList)

  def writeResult(a: UserCommand): Future[UserCommand] =
    DataBaseIO
      .writeEntity[UserCommand, UserCommandTable](
        TableQuery[UserCommandTable],
        a
      )

  def deleteQueryResult(a: UserCommand): Future[String] = DataBaseIO
    .updateEntity(
      TableQuery[UserCommandTable].filter(_.id === a.id).delete
    )
    .map(x => x.toString)

  val commandsRoute: Route =
    Directives.get { ctx =>
      {
        ctx.complete {
          filteredTermsQueryResult
        }
      }
    } ~
      post {
        entity(as[UserCommand]) { command =>
          complete {
            writeResult(command)
          }
        }
      } ~
      delete {
        entity(as[UserCommand]) { command =>
          complete {
            deleteQueryResult(command)
          }
        }
      }

}
