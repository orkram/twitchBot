package customCommands.commands

trait WithTwitchOutput {
  val params: List[String]

  def outputCommands(): List[String]
}
