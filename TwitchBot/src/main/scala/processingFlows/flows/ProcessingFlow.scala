package processingFlows.flows

import akka.NotUsed
import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl.Flow

trait ProcessingFlow[O <: Nothing] {

  def isApplicableFor(m: Message): Boolean

  val queue: String //rabbitMq queue where messages are being read

  def parseMessage(m: Message): O

  def process(): Flow[Message, O, NotUsed]

}
