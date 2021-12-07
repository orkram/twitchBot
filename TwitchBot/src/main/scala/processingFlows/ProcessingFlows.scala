package processingFlows

import akka.stream.alpakka.amqp.AmqpConnectionProvider
import processingFlows.common.ProcessingFlow
import processingFlows.flows.{
  CustomCommandFlow,
  FilteringFlow,
  UrlFilteringFlow,
  UserCommandFlow
}

object ProcessingFlows {

  def flows(
      connectionProvider: AmqpConnectionProvider
  ): List[ProcessingFlow[_]] = {
    List(
      UserCommandFlow(connectionProvider),
      CustomCommandFlow(connectionProvider),
      UrlFilteringFlow(connectionProvider),
      FilteringFlow(connectionProvider)
    )
  }
}
