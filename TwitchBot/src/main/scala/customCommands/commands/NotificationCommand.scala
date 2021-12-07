package customCommands.commands

import model.RecurringNotification

case class NotificationCommand(rn: RecurringNotification)
    extends WithTwitchOutput {

  override def outputCommands: List[String] = List(
    s"PRIVMSG #sadbruh1 :${rn.notification}"
  )
}
