package rabbitmq

import akka.Done
import akka.stream.alpakka.amqp._
import akka.stream.alpakka.amqp.scaladsl.AmqpFlow
import akka.stream.scaladsl.Flow
import common.MessageLogger.logMessage

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

case class RmqMessageWriterFlow(queueName: String) {

  val queueDeclaration: QueueDeclaration = QueueDeclaration(queueName)

  val connectionProvider: AmqpUriConnectionProvider = AmqpUriConnectionProvider(
    "amqp://localhost:5672"
  )

  val settings: AmqpWriteSettings = AmqpWriteSettings(connectionProvider)
    .withRoutingKey(queueName)
    .withDeclaration(queueDeclaration)
    .withBufferSize(20)
    .withConfirmationTimeout(200.millis)

  val amqpFlow: Flow[WriteMessage, WriteResult, Future[Done]] =
    AmqpFlow
      .withConfirm(settings)
      .map(m => logMessage(m))
}
