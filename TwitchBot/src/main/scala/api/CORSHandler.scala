package api

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}
import scala.language.postfixOps

trait CORSHandler {

  private val corsResponseHeaders = List(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`(
      "Access-Control-Allow-Headers",
      "Access-Control-Allow-Methods",
      "Access-Control-Allow-Origin",
      "Authorization",
      "Content-Type",
      "X-Requested-With"
    )
  )

  private def addAccessControlHeaders(): Directive0 = {
    respondWithHeaders(corsResponseHeaders)
  }
  //clueless
  private def preflightRequestHandler: Route = options {
    complete(
      HttpResponse(StatusCodes.OK).withHeaders(
        `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)
      )
    )
  }

  def corsHandler(r: Route): Route = addAccessControlHeaders() {
    preflightRequestHandler ~ r
  }

  def addCORSHeaders(response: HttpResponse): HttpResponse =
    response.withHeaders(corsResponseHeaders)

}
