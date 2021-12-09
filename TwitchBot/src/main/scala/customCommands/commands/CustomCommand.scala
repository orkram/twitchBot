package customCommands.commands

import common.TwitchMessage

import scala.concurrent.Future

trait CustomCommand[O <: WithTwitchOutput] {
  val signature: String

  def apply(tm: TwitchMessage, m: List[String]): Future[Option[O]]
}
