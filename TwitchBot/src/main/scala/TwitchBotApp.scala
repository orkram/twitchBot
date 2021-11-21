import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.Materializer
import akka.stream.alpakka.amqp.{WriteMessage, WriteResult}
import akka.stream.scaladsl._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import common.{ConfigLoader, TwitchMessage}
import common.MessageLogger.logMessage
import configs.TwitchWsConfig
import org.json4s.DefaultFormats
import rabbitmq.{RmqMessageReaderFlow, RmqMessageWriterFlow}
import org.json4s.jackson.Serialization.{read, write}

import scala.concurrent.Promise

object TwitchBotApp extends App {

  val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("mySystem")
  implicit val mat: Materializer = Materializer(system)

  val twitchWsConfig = ConfigLoader.loadConfig(classOf[TwitchWsConfig])
  val request = WebSocketRequest(twitchWsConfig.url)

  val rmqFlow = RmqMessageWriterFlow("test-consumer").amqpFlow

  implicit val formats: DefaultFormats.type = DefaultFormats

  val toRmqSink = Flow[Message]
    .map(logMessage)
    .map(m => TwitchMessage(m.asTextMessage.getStrictText))
    .map {
      case x: TwitchMessage if x.message.nonEmpty => x
    }
    .map(tm => WriteMessage(ByteString(write(tm))))
    .via(rmqFlow)
    .to(Sink.ignore)

  val fromRmq: Source[TextMessage, NotUsed] =
    RmqMessageReaderFlow("twitch-command-queue").amqpSource.map(s =>
      TextMessage(s.bytes.decodeString(ByteString.UTF_8))
    )

  val initRmqConnectionSource = Source(
    List(
      TextMessage("PASS " + twitchWsConfig.pass),
      TextMessage("NICK " + twitchWsConfig.nickname),
      TextMessage("JOIN #" + twitchWsConfig.nickname)
    )
  )

  val flow: Flow[Message, Message, Promise[Option[Message]]] =
    Flow.fromSinkAndSourceMat(
      toRmqSink,
      Source
        .combine(initRmqConnectionSource, fromRmq)(Concat(_))
        .map(logMessage)
        .concatMat(Source.maybe[Message])(Keep.right)
    )(Keep.right)

  val (upgradeResponse, promise) =
    Http().singleWebSocketRequest(request, flow)
}
