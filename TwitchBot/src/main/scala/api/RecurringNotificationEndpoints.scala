package api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import db.DataBaseIO
import model.{RecurringNotification, RecurringNotificationTable}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RecurringNotificationEndpoints() {

  def notificationQueryResult: Future[List[RecurringNotification]] =
    DataBaseIO
      .readEntities[RecurringNotification, RecurringNotificationTable](
        TableQuery[RecurringNotificationTable]
      )
      .map(_.toList)

  def writeResult(a: RecurringNotification): Future[RecurringNotification] =
    DataBaseIO
      .writeEntity[RecurringNotification, RecurringNotificationTable](
        TableQuery[RecurringNotificationTable],
        a
      )

  def deleteQueryResult(a: RecurringNotification): Future[String] = DataBaseIO
    .updateEntity(
      TableQuery[RecurringNotificationTable].filter(_.id === a.id).delete
    )
    .map(x => x.toString)

  val ordersRoute: Route =
    Directives.get { ctx =>
      {
        ctx.complete {
          notificationQueryResult
        }
      }
    } ~
      post {
        entity(as[RecurringNotification]) { notification =>
          complete {
            writeResult(notification)
          }
        }
      } ~
      delete {
        entity(as[RecurringNotification]) { notification =>
          complete {
            deleteQueryResult(notification)
          }
        }
      }

}
