package api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import db.DataBaseIO
import model.{FilteredTerm, FilteredTermTable}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class FilteredTermsEndpoints() {

  def filteredTermsQueryResult: Future[List[FilteredTerm]] =
    DataBaseIO
      .readEntities[FilteredTerm, FilteredTermTable](
        TableQuery[FilteredTermTable]
      )
      .map(_.toList)

  def writeResult(a: FilteredTerm): Future[FilteredTerm] =
    DataBaseIO
      .writeEntity[FilteredTerm, FilteredTermTable](
        TableQuery[FilteredTermTable],
        a
      )

  def deleteQueryResult(a: FilteredTerm): Future[String] = DataBaseIO
    .updateEntity(
      TableQuery[FilteredTermTable].filter(_.id === a.id).delete
    )
    .map(x => x.toString)

  val termsRoute: Route =
    Directives.get { ctx =>
      {
        ctx.complete {
          filteredTermsQueryResult
        }
      }
    } ~
      post {
        entity(as[FilteredTerm]) { term =>
          complete {
            writeResult(term)
          }
        }
      } ~
      delete {
        entity(as[FilteredTerm]) { term =>
          complete {
            deleteQueryResult(term)
          }
        }
      }
}
