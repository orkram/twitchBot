package customCommands.commands

case class TestCommand(
    dummyParameter1: String,
    dummyParameter2: String
) extends WithTwitchOutput {
  override val params: List[String] = List(dummyParameter1, dummyParameter2)

  override def outputCommands: List[String] = {
    List(s"PRIVMSG #sadbruh1 : $dummyParameter1")
  }
}

object TestCommand extends CustomCommand[TestCommand] {
  override val signature = "test"

  override def apply(parameters: List[String]): Option[TestCommand] = {
    parameters match {
      case List(par1, par2, _*) => Some(TestCommand(par1, par2))
      case _                    => None //either?
    }

  }
}
