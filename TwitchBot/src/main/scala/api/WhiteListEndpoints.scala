package api

import slick.jdbc.PostgresProfile.api._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import db.DataBaseIO
import model.{WhiteListedDomain, WhiteListedDomainTable}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class WhiteListEndpoints() {

  def whiteListQueryResult: Future[List[WhiteListedDomain]] =
    DataBaseIO
      .readEntities[WhiteListedDomain, WhiteListedDomainTable](
        TableQuery[WhiteListedDomainTable]
      )
      .map(_.toList)

  def writeResult(a: WhiteListedDomain): Future[WhiteListedDomain] =
    DataBaseIO
      .writeEntity[WhiteListedDomain, WhiteListedDomainTable](
        TableQuery[WhiteListedDomainTable],
        a
      )

  def deleteQueryResult(a: WhiteListedDomain): Future[String] = DataBaseIO
    .updateEntity(
      TableQuery[WhiteListedDomainTable].filter(_.id === a.id).delete
    )
    .map(x => x.toString)

  val ordersRoute: Route =
    Directives.get { ctx =>
      {
        ctx.complete {
          whiteListQueryResult
        }
      }
    } ~
      post {
        entity(as[WhiteListedDomain]) { domain =>
          complete {
            writeResult(domain)
          }
        }
      } ~
      delete {
        entity(as[WhiteListedDomain]) { domain =>
          complete {
            deleteQueryResult(domain)
          }
        }
      }
}
