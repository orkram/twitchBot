import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.amqp.AmqpUriConnectionProvider
import com.typesafe.config.ConfigFactory
import common.ConfigLoader
import common.MessageLogger.logMessage
import configs.TwitchWsConfig
import processingFlows.ProcessingFlows
import twitchWebsocket.TwitchWebSocketConnection

object TwitchBotApp extends App {

  val config = ConfigFactory.load()

  val twitchWsConfig = ConfigLoader.loadConfig(classOf[TwitchWsConfig])

  val connectionProvider: AmqpUriConnectionProvider = AmqpUriConnectionProvider(
    "amqp://localhost:5672"
  )
  implicit val system: ActorSystem = ActorSystem("mySystem1")
  implicit val mat: Materializer = Materializer(system)
  val queues = ProcessingFlows.flows(connectionProvider).map(_.queueName)

  val (upgradeResponse, promise) =
    TwitchWebSocketConnection(
      twitchWsConfig,
      connectionProvider,
      queues
    )
      .establishConnection()

  val processingFlows =
    ProcessingFlows
      .flows(connectionProvider)
      .map { graph =>
        logMessage("Staring graph: " + graph.queueName)
        graph.getGraph.run()
      }
  //TODO: restart logic, and error handling
}
