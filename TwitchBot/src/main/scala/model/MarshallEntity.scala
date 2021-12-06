package model

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest}
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.stream.Materializer
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
abstract class MarshallEntity[A <: AnyRef](implicit manifect: Manifest[A]) {

  implicit val formats: Formats =
    DefaultFormats + OffsetDateTimeSerializer

  implicit val marshaller: ToEntityMarshaller[A] = {
    Marshaller.withFixedContentType(ContentTypes.`application/json`) { obj =>
      HttpEntity(
        ContentTypes.`application/json`,
        Serialization.write[A](obj).getBytes("UTF-8")
      )
    }
  }

  implicit val listMarshaller: ToEntityMarshaller[List[A]] = {
    Marshaller.withFixedContentType(ContentTypes.`application/json`) { obj =>
      HttpEntity(
        ContentTypes.`application/json`,
        Serialization.write[List[A]](obj).getBytes("UTF-8")
      )
    }
  }

  implicit val unmarshaller: FromRequestUnmarshaller[A] =
    new FromRequestUnmarshaller[A] {
      override def apply(request: HttpRequest)(implicit
          ec: ExecutionContext,
          materializer: Materializer
      ): Future[A] =
        request.entity
          .toStrict(5 seconds)
          .map(_.data.decodeString("UTF-8"))
          .map { str =>
            read[A](str)
          }
    }
}
