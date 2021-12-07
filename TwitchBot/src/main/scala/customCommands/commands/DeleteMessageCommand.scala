package customCommands.commands

import common.TwitchMessage

case class DeleteMessageCommand(tm: TwitchMessage) extends WithTwitchOutput {

  override def outputCommands: List[String] = List(
    s"PRIVMSG #sadbruh1 :/delete ${tm.id.get}"
  ) ++ TimeoutCommand(tm).outputCommands
}
