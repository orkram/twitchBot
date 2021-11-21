package customCommands.commands

trait CustomCommand[O <: WithTwitchOutput] {
  val signature: String

  def apply(m: List[String]): Option[O]
}
