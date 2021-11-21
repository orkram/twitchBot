package twitchWebsocket

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{
  Message,
  TextMessage,
  WebSocketRequest,
  WebSocketUpgradeResponse
}
import akka.stream.{ClosedShape, Materializer, OverflowStrategy}
import akka.stream.alpakka.amqp.{AmqpConnectionProvider, WriteMessage}
import akka.stream.scaladsl.MergeHub.source
import akka.stream.scaladsl.{
  Broadcast,
  Concat,
  Flow,
  GraphDSL,
  Keep,
  Merge,
  RunnableGraph,
  Sink,
  Source
}
import akka.util.ByteString
import common.MessageLogger.logMessage
import common.TwitchMessage
import configs.TwitchWsConfig
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write
import rabbitmq.{RmqMessageReaderFlow, RmqMessageWriterFlow}

import scala.concurrent.{Future, Promise}

case class TwitchWebSocketConnection(
    config: TwitchWsConfig,
    connectionProvider: AmqpConnectionProvider,
    queues: List[String]
) {
  implicit val system: ActorSystem = ActorSystem("mySystem")
  implicit val mat: Materializer = Materializer(system)

  implicit val formats: DefaultFormats.type = DefaultFormats

  private val request = WebSocketRequest(config.url)

  private val rmqWriterFlow = {
    RmqMessageWriterFlow("custom-command-queue", connectionProvider).amqpFlow
  }

  private val toRmqSink = Flow[Message]
    .map(logMessage)
    .map(m => TwitchMessage(m.asTextMessage.getStrictText))
    .map {
      case x: TwitchMessage if x.message.nonEmpty => x
    }
    .map(tm => WriteMessage(ByteString(write(tm))))
    .via(rmqWriterFlow)
    .to(Sink.ignore)

  private val fromRmq: Source[TextMessage, NotUsed] =
    RmqMessageReaderFlow("twitch-command-queue", connectionProvider).amqpSource
      .map(s => TextMessage(s.bytes.decodeString(ByteString.UTF_8)))

  val initRmqConnectionSource: Source[TextMessage.Strict, NotUsed] = Source(
    List(
      TextMessage("PASS " + config.pass),
      TextMessage("NICK " + config.nickname),
      TextMessage("JOIN #" + config.nickname)
    )
  )

  private val flow: Flow[Message, Message, Promise[Option[Message]]] =
    Flow.fromSinkAndSourceMat(
      toRmqSink,
      Source
        .combine(initRmqConnectionSource, fromRmq)(Concat(_))
        .map(logMessage)
        .concatMat(Source.maybe[Message])(Keep.right)
    )(Keep.right)

  def establishConnection()
      : (Future[WebSocketUpgradeResponse], Promise[Option[Message]]) =
    Http().singleWebSocketRequest(request, flow)
}
