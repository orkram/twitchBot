package api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import model.WhiteListedDomain

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

case class ConfigurationRoutes()(implicit ec: ExecutionContext) {

  val testResponse = WhiteListedDomain(1, "youtube.com/")

  val baseRoute: Route =
    path("whiteList") {
      get {
        complete(
          testResponse
        )
      } ~
        post {
          entity(as[WhiteListedDomain]) { list =>
            complete {
              Future(list)
            }
          }
        }
    }

}
