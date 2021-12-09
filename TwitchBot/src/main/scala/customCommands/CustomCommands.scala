package customCommands

import customCommands.commands.{
  BetCommand,
  CheckPointsCommand,
  CustomCommand,
  TestCommand,
  WithTwitchOutput
}

object CustomCommands {
  def knownCommands(): List[CustomCommand[_ <: WithTwitchOutput]] = List(
    TestCommand,
    BetCommand,
    CheckPointsCommand
  )
}
