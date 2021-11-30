package api

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.stream.Materializer
import model.WhiteListedDomain
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

case class ConfigurationRoutes()(implicit ec: ExecutionContext) {

  implicit val formats: DefaultFormats = DefaultFormats

  implicit val whiteListMarshaller: ToEntityMarshaller[WhiteListedDomain] = {
    Marshaller.withFixedContentType(ContentTypes.`application/json`) { obj =>
      HttpEntity(
        ContentTypes.`application/json`,
        Serialization.write(obj).getBytes("UTF-8")
      )
    }
  }

  val testResponse = WhiteListedDomain(1, "youtube.com/")

  implicit val whiteListUnMarshaller: FromRequestUnmarshaller[
    WhiteListedDomain
  ] = //todo move to some json4s support class where this would be generic and implicit
    new FromRequestUnmarshaller[WhiteListedDomain] {
      override def apply(request: HttpRequest)(implicit
          ec: ExecutionContext,
          materializer: Materializer
      ): Future[WhiteListedDomain] =
        request.entity
          .toStrict(5 seconds)
          .map(_.data.decodeString("UTF-8"))
          .map { str =>
            read[WhiteListedDomain](str)
          }
    }

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
