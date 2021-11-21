package customCommands

import customCommands.commands.{CustomCommand, TestCommand, WithTwitchOutput}

object CustomCommands {
  def knownCommands(): List[CustomCommand[_ <: WithTwitchOutput]] = List(
    TestCommand
  )
}
