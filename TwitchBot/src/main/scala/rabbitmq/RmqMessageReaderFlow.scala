package rabbitmq

import akka.NotUsed
import akka.stream.alpakka.amqp.scaladsl.AmqpSource
import akka.stream.alpakka.amqp.{
  AmqpConnectionProvider,
  NamedQueueSourceSettings,
  QueueDeclaration,
  ReadResult
}
import akka.stream.scaladsl.Source

case class RmqMessageReaderFlow(
    queueName: String,
    connectionProvider: AmqpConnectionProvider
) {

  val queueDeclaration: QueueDeclaration = QueueDeclaration(queueName)

  val amqpSource: Source[ReadResult, NotUsed] =
    AmqpSource.atMostOnceSource(
      NamedQueueSourceSettings(connectionProvider, queueName)
        .withDeclaration(queueDeclaration)
        .withAckRequired(false),
      bufferSize = 20
    )

}
