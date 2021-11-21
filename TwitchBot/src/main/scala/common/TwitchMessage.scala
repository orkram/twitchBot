package common

import scala.util.matching.Regex

case class TwitchMessage(
    command: Option[String] = None,
    channel: Option[String] = None,
    nickname: Option[String] = None,
    message: Option[String] = None
) {

  //methods do parse message to get commands out of it
}

object TwitchMessage {

  //:aristol1738!aristol1738@aristol1738.tmi.twitch.tv PRIVMSG #sadbruh1 :bb
  val messagePattern: Regex = """(?<=:(?!.*:))(.*?)(?=$)""".r
  val commandPattern: Regex = """(?<=\s)(.*?)(?=\s)""".r
  val nicknamePattern: Regex = """(?<=:)(.*?)(?=!)""".r
  val channelPattern: Regex = """(?<=#)(.*?)(?=\s)""".r
  def apply(m: String): TwitchMessage = {
    TwitchMessage(
      command = commandPattern
        .findFirstMatchIn(m)
        .map(_.toString),
      channel = channelPattern
        .findFirstMatchIn(m)
        .map(_.toString),
      nickname = nicknamePattern
        .findFirstMatchIn(m)
        .map(_.toString),
      message = messagePattern
        .findFirstMatchIn(m)
        .map(_.toString)
    )
  }

}
