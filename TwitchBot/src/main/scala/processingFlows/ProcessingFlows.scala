package processingFlows

import akka.stream.alpakka.amqp.AmqpConnectionProvider
import processingFlows.flows.{CustomCommandFlow, ProcessingFlow}

object ProcessingFlows {

  def flows(
      connectionProvider: AmqpConnectionProvider
  ): List[ProcessingFlow[_]] = {
    List(CustomCommandFlow(connectionProvider))
  }
}
