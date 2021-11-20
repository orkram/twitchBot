import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.Materializer
import akka.stream.alpakka.amqp.{WriteMessage, WriteResult}
import akka.stream.scaladsl._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import common.ConfigLoader
import common.MessageLogger.logMessage
import configs.TwitchWsConfig
import rabbitmq.RmqMessageWriterFlow

import scala.concurrent.Promise

object TwitchBotApp extends App {

  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("mySystem")
  implicit val mat: Materializer = Materializer(system)

  val twitchWsConfig = ConfigLoader.loadConfig(classOf[TwitchWsConfig])
  val request = WebSocketRequest(twitchWsConfig.url)

  val toRmqSink = Flow[Message]
    .map(logMessage)
    .map(m => TwitchMessage(m.asTextMessage.getStrictText))
    .map {
      case x: TwitchMessage if x.message.nonEmpty => x
    }
    .map(tm => WriteMessage(ByteString(tm.toString)))
    .via(RmqMessageWriterFlow("test-consumer").amqpFlow)
    .to(Sink.ignore)

  val flow: Flow[Message, Message, Promise[Option[Message]]] =
    Flow.fromSinkAndSourceMat(
      toRmqSink,
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
