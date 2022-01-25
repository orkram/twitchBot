package api

import akka.http.scaladsl.server.Directives.{path, _}
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import configs.TwitchAmpqConfig

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

case class ConfigurationRoutes(ampqConfig: TwitchAmpqConfig)(implicit
    ec: ExecutionContext,
    mat: Materializer
) {

  val baseRoute: Route =
    path("whiteList")(WhiteListEndpoints().ordersRoute) ~
      path("recurringNotification")(
        RecurringNotificationEndpoints().ordersRoute
      ) ~ path("startBet")(
        BettingEndpoints(ampqConfig).startBetRoute
      ) ~ path("finishBet")(
        BettingEndpoints(ampqConfig).finishBetRoute
      ) ~ path("filteredTerms")(
        FilteredTermsEndpoints().termsRoute
      ) ~ path("betState")(
        BettingEndpoints(ampqConfig).getBetStateRoute
      ) ~ path("customCommand")(
        CustomCommandEndpoints().commandsRoute
      )

}
