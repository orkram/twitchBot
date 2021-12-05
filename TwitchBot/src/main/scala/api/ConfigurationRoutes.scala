package api

import slick.jdbc.PostgresProfile.api._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import db.DataBaseIO
import model.{WhiteListedDomain, WhiteListedDomainTable}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

case class ConfigurationRoutes()(implicit ec: ExecutionContext) {

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
    .removeEntity(
      TableQuery[WhiteListedDomainTable].filter(_.id === a.id).delete
    )
    .map(x => x.toString)

  val baseRoute: Route = concat(
    path("whiteList")(ordersRoute)
  )

  lazy val ordersRoute: Route =
    get { ctx =>
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
