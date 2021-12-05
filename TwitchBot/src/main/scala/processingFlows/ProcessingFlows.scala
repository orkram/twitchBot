package processingFlows

import akka.stream.alpakka.amqp.AmqpConnectionProvider
import processingFlows.common.ProcessingFlow
import processingFlows.flows.{CustomCommandFlow, UrlFilteringFlow}

object ProcessingFlows {

  def flows(
      connectionProvider: AmqpConnectionProvider
  ): List[ProcessingFlow[_]] = {
    List(
      CustomCommandFlow(connectionProvider),
      UrlFilteringFlow(connectionProvider)
    )
  }
}
