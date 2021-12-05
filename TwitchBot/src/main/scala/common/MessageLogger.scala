package common

import akka.event.slf4j.Logger
import akka.http.scaladsl.model.ws.Message

object MessageLogger {

  val logger = Logger("Root")

  def logMessage[A](message: A): A = {
    logger.debug(
      message.toString
    )
    message
  }

  def logMessage(tm: TwitchMessage): TwitchMessage = {
    logger.debug(tm.toString)
    tm
  }

  def logMessage(message: Message): Message = {
    logger.debug(
      message.asTextMessage.getStrictText
    )
    message
  }

}
