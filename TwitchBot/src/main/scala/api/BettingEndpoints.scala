package api

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import akka.stream.alpakka.amqp.{
  AmqpUriConnectionProvider,
  WriteMessage,
  WriteResult
}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import common.MessageLogger.logMessage
import configs.TwitchAmpqConfig
import customCommands.commands.BetChangedNotification
import db.DataBaseIO
import model._
import rabbitmq.RmqMessageWriterFlow
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContextExecutor, Future}

case class BettingEndpoints(ampqConfig: TwitchAmpqConfig) {

  def connectionProvider: AmqpUriConnectionProvider = AmqpUriConnectionProvider(
    ampqConfig.url
  )

  def notifyTwitch: Flow[WriteMessage, WriteResult, Future[Done]] =
    RmqMessageWriterFlow(
      "twitch-command-queue",
      connectionProvider
    ).amqpFlow

  val betChangedNotification: BetChangedNotification = BetChangedNotification(
    "Bet has started, time to use your points. Use !bet win/lose amount"
  )

  def betfinishedNotification(outcome: String): BetChangedNotification =
    BetChangedNotification(
      s"Bet has finished as $outcome. Check your points!"
    )

  def betCompletedNotification(b: Bettor, o: String): BetChangedNotification = {

    val outcome = {
      if (b.outcome != o) { "lost" }
      else { "won" }
    }

    BetChangedNotification(
      s"/w ${b.nickname} You have $outcome ${b.unsafePool} points"
    )

  }

  def writeToTwitch(msg: BetChangedNotification): Future[Seq[WriteResult]] = {
    Source(
      List(
        msg
      )
    )
      .mapConcat(c => c.outputCommands.map(x => WriteMessage(ByteString(x))))
      .via(notifyTwitch)
      .runWith(Sink.seq)
  }

  implicit val system: ActorSystem[Nothing] =
    ActorSystem(Behaviors.empty, "betting-system")

  implicit val executionContext: ExecutionContextExecutor =
    system.executionContext

  implicit val mat: Materializer = Materializer(system)

  def startBet: Future[Option[BetSession]] = {
    val anyOngoingBets = DataBaseIO
      .readEntities[BetSession, BetSessionTable](
        TableQuery[BetSessionTable].filter(b => b.state === "ongoing")
      )
      .map(_.toList)

    anyOngoingBets.flatMap {
      case Nil =>
        {
          writeToTwitch(betChangedNotification)
            .flatMap { _ =>
              DataBaseIO
                .writeEntity[BetSession, BetSessionTable](
                  TableQuery[BetSessionTable],
                  BetSession(1, "ongoing", 1000, 1000)
                )
            }
        }.map(x => Some(x))
      case _ => Future.successful(None)
    }

  }

  private def finishAllBets(
      finishedSession: BetSession,
      outcome: String
  ): Future[Int] = {

    val bettorsUpdates = DataBaseIO
      .readEntities[Bettor, BettorTable](
        TableQuery[BettorTable]
          .filter(en => !(en.outcome === "u"))
      )
      .flatMap { bettors =>
        val writesToTwitch = bettors
          .map(b => betCompletedNotification(b, outcome))
          .appended(betfinishedNotification(outcome))
          .map(writeToTwitch)
          .map(f => f.map(_ => 1))

        val writesToDb = bettors
          .map(_.finishBet(finishedSession.ratio(outcome), outcome))
          .map { b =>
            DataBaseIO.updateEntity[BetSession, BetSessionTable](
              TableQuery[BettorTable]
                .filter(en => en.id === b.id)
                .update(b)
            )
          }

        Future.sequence(
          writesToDb ++ writesToTwitch
        )
      }

    bettorsUpdates.flatMap(_ =>
      //update betSession
      DataBaseIO.updateEntity[BetSession, BetSessionTable](
        TableQuery[BetSessionTable]
          .filter(en => en.id === finishedSession.id)
          .update(finishedSession)
      )
    )

  }

  def finishBet(outcome: String): Future[Option[BetSession]] = {

    logMessage(s"Finishing ongoing bet as $outcome")

    val anyOngoingBetSessions = DataBaseIO
      .readEntities[BetSession, BetSessionTable](
        TableQuery[BetSessionTable]
      )
      .map(_.toList)

    anyOngoingBetSessions.flatMap { l =>
      l.filter(_.state == "ongoing") match {
        case Nil => Future.successful(None)
        case head :: _ =>
          val finishedSession = head.copy(state = "finished")
          finishAllBets(finishedSession, outcome).map(_ =>
            Some(head.copy(state = "finished"))
          )
      }
    }
  }

  def getCurrentBet: Future[Option[BetSession]] = {
    val anyOngoingBetSessions = DataBaseIO
      .readEntities[BetSession, BetSessionTable](
        TableQuery[BetSessionTable].filter(bet => bet.state === "ongoing")
      )
      .map(_.toList)
    anyOngoingBetSessions.map(_.headOption)
  }

  def startBetRoute: Route =
    Directives.post { ctx =>
      {
        ctx.complete {
          startBet
        }
      }
    }

  def finishBetRoute: Route =
    Directives.post {
      entity(as[Outcome]) { outcome =>
        complete {
          finishBet(outcome.outcome)
        }
      }
    } ~ Directives.post { ctx =>
      {
        ctx.complete {
          Future("a")
        }
      }
    }

  val getBetStateRoute: Route =
    Directives.get {
      complete {
        getCurrentBet
      }
    }
}
