import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.Materializer
import akka.stream.scaladsl._
import com.typesafe.config.ConfigFactory
import common.{ConfigLoader, Logging}
import twitchWebsocket.TwitchWsConfig

import scala.concurrent.Promise

object TwitchBotApp extends App with Logging {

  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("mySystem")
  implicit val mat: Materializer = Materializer(system)

  val twitchWsConfig = ConfigLoader.loadConfig(classOf[TwitchWsConfig])
  val request = WebSocketRequest(twitchWsConfig.url)

  val flow: Flow[Message, Message, Promise[Option[Message]]] =
    Flow.fromSinkAndSourceMat(
      Sink
        .seq[Message]
        .contramap[Message](x => logMessage(x)),
      Source(
        List(
          TextMessage("PASS " + twitchWsConfig.pass),
          TextMessage("NICK " + twitchWsConfig.nickname),
          TextMessage("JOIN #" + twitchWsConfig.nickname)
        )
      )
        .map(logMessage)
        .concatMat(Source.maybe[Message])(Keep.right)
    )(Keep.right)

  val (upgradeResponse, promise) =
    Http().singleWebSocketRequest(request, flow)
}
