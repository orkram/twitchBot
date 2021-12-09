package api

import akka.http.scaladsl.server.Directives.{path, _}
import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

case class ConfigurationRoutes()(implicit ec: ExecutionContext) {

  val baseRoute: Route =
    path("whiteList")(WhiteListEndpoints().ordersRoute) ~
      path("recurringNotification")(
        RecurringNotificationEndpoints().ordersRoute
      ) ~ path("startBet")(
        BettingEndpoints().startBetRoute
      ) ~ path("finishBet")(
        BettingEndpoints().finishBetRoute
      )

}
