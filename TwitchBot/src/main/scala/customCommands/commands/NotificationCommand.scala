package customCommands.commands

import model.RecurringNotification

case class NotificationCommand(rn: RecurringNotification)
    extends WithTwitchOutput {
  override val params: List[String] = Nil

  override def outputCommands: List[String] = List(
    s"PRIVMSG #sadbruh1 :${rn.notification}"
  )
}
