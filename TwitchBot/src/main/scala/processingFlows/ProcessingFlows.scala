package processingFlows

import akka.stream.alpakka.amqp.AmqpConnectionProvider
import processingFlows.common.ProcessingFlow
import processingFlows.flows.{
  CustomCommandFlow,
  FilteringFlow,
  UrlFilteringFlow
}

object ProcessingFlows {

  def flows(
      connectionProvider: AmqpConnectionProvider
  ): List[ProcessingFlow[_]] = {
    List(
      CustomCommandFlow(connectionProvider),
      UrlFilteringFlow(connectionProvider),
      FilteringFlow(connectionProvider)
    )
  }
}
