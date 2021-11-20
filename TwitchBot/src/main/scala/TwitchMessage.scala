case class TwitchMessage(
    command: Option[String] = None,
    channel: Option[String] = None,
    nickname: Option[String] = None,
    message: Option[String] = None
) {

  //methods do parse message to get commands out of it
}

object TwitchMessage {
  def apply(m: String): TwitchMessage = {

    //:sadbruh1!sadbruh1@sadbruh1.tmi.twitch.tv PRIVMSG #sadbruh1 :14

    TwitchMessage(
      command = """(?<=\s)(.*?)(?=\s)""".r
        .findFirstMatchIn(m)
        .map(_.toString),
      channel = """(?<=:)(.*?)(?=!)""".r
        .findFirstMatchIn(m)
        .map(_.toString),
      nickname = """(?<=#)(.*?)(?=\s)""".r
        .findFirstMatchIn(m)
        .map(_.toString),
      message = """(?<=:(?!.*:))(.*?)(?=$)""".r
        .findFirstMatchIn(m)
        .map(_.toString)
    )
  }

  def parseMessage(s: String) = ???
}
