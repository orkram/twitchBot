package customCommands.commands

import common.TwitchMessage

import scala.concurrent.Future

case class TestCommand(
    dummyParameter1: String,
    dummyParameter2: String
) extends WithTwitchOutput {

  override def outputCommands: List[String] = {
    List(s"PRIVMSG #sadbruh1 : $dummyParameter1")
  }
}

object TestCommand extends CustomCommand[TestCommand] {
  override val signature = "test"

  override def apply(
      tm: TwitchMessage,
      parameters: List[String]
  ): Future[Option[TestCommand]] = {
    parameters match {
      case List(par1, par2, _*) =>
        Future.successful(Some(TestCommand(par1, par2)))
      case _ => Future.successful(None)
    }

  }
}
