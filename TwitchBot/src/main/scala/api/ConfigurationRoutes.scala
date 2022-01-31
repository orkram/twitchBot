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
) extends CORSHandler {

  val bettingEndpoints: BettingEndpoints = BettingEndpoints(ampqConfig)

  val baseRoute: Route =
    corsHandler(
      path("whiteList")(WhiteListEndpoints().ordersRoute) ~
        path("recurringNotification")(
          RecurringNotificationEndpoints().ordersRoute
        ) ~ path("startBet")(
          bettingEndpoints.startBetRoute
        ) ~ path("finishBet")(
          bettingEndpoints.finishBetRoute
        ) ~ path("filteredTerms")(
          FilteredTermsEndpoints().termsRoute
        ) ~ path("betState")(
          bettingEndpoints.getBetStateRoute
        ) ~ path("customCommand")(
          CustomCommandEndpoints().commandsRoute
        )
    )

}
