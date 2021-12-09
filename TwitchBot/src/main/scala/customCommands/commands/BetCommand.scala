package customCommands.commands

import common.TwitchMessage

import scala.concurrent.Future

case class BetCommand(
    outcome: String,
    ammount: String
) extends WithTwitchOutput {

  override def outputCommands: List[String] = {
    List(s"PRIVMSG #sadbruh1 :/w You have bet $ammount on $outcome")
  }
}

object BetCommand extends CustomCommand[TestCommand] {
  override val signature = "bet"

  override def apply(
      tm: TwitchMessage,
      m: List[String]
  ): Future[Option[TestCommand]] = {

    //create if doesn't exist for nickname

    //then apply bet if parameters are correct

    //return output for Twitch
//    if(m.size == 2 && m.last.){
//      val outcome = m.head match{
//        case "win" => BetCommand("w", m.last)
//        case "lose" => BetCommand("l", m.last)
//        case "" => None
//      }else{
//        None
//      }
//    }
    ???
  }
}
