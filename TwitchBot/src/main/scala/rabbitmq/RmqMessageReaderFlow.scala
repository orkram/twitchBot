package rabbitmq

import akka.NotUsed
import akka.stream.alpakka.amqp.scaladsl.AmqpSource
import akka.stream.alpakka.amqp.{
  AmqpUriConnectionProvider,
  NamedQueueSourceSettings,
  QueueDeclaration,
  ReadResult
}
import akka.stream.scaladsl.Source

case class RmqMessageReaderFlow(queueName: String) {

  val queueDeclaration: QueueDeclaration = QueueDeclaration(queueName)

  val connectionProvider: AmqpUriConnectionProvider = AmqpUriConnectionProvider(
    "amqp://localhost:5672"
  )

  val amqpSource: Source[ReadResult, NotUsed] =
    AmqpSource.atMostOnceSource(
      NamedQueueSourceSettings(connectionProvider, queueName)
        .withDeclaration(queueDeclaration)
        .withAckRequired(false),
      bufferSize = 10
    )

}
