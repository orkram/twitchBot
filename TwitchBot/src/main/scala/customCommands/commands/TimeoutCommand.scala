package customCommands.commands

import common.TwitchMessage

case class TimeoutCommand(tm: TwitchMessage) extends WithTwitchOutput {

  override def outputCommands: List[String] = List(
    s"PRIVMSG #sadbruh1 :/timeout ${tm.nickname.get} 30 banned keyword"
  )
}
