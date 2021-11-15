package common

import akka.event.slf4j.Logger
import akka.http.scaladsl.model.ws.Message

trait Logging {

  val logger = Logger("Root")

  def logMessage[A <: Message](message: A): A = {
    logger.debug(
      "Sending message of socket: " + message.asTextMessage.getStrictText
    )
    message
  }
}
