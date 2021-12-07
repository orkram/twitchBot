package customCommands.commands

import common.TwitchMessage

case class SimpleOutputCommandResponse(tm: TwitchMessage, output: String)
    extends WithTwitchOutput {

  override def outputCommands: List[String] = List(
    s"PRIVMSG #sadbruh1 : @${tm.nickname.get} $output"
  )
}
