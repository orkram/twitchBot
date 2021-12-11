package customCommands.commands

case class BetChangedNotification(betMessage: String) extends WithTwitchOutput {

  override def outputCommands: List[String] = List(
    s"PRIVMSG #sadbruh1 :$betMessage"
  )
}
