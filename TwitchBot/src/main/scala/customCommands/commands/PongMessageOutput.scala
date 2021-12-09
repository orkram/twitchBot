package customCommands.commands

import common.TwitchMessage

case class PongMessageOutput(tm: TwitchMessage) extends WithTwitchOutput {

  override def outputCommands: List[String] = List(
    s"PONG :${tm.message.get}"
  )
}
