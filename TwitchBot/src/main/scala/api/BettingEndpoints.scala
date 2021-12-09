package api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import common.MessageLogger.logMessage
import db.DataBaseIO
import model._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class BettingEndpoints() {

  def startBet: Future[Option[BetSession]] = {
    val anyOngoingBets = DataBaseIO
      .readEntities[BetSession, BetSessionTable](
        TableQuery[BetSessionTable].filter(b => b.state === "ongoing")
      )
      .map(_.toList)

    anyOngoingBets.flatMap {
      case Nil =>
        {
          DataBaseIO
            .writeEntity[BetSession, BetSessionTable](
              TableQuery[BetSessionTable],
              BetSession(1, "ongoing", 1000, 1000)
            )
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
        Future.sequence(
          bettors.map(_.finishBet(finishedSession.ratio, outcome)).map { b =>
            DataBaseIO.updateEntity[BetSession, BetSessionTable](
              TableQuery[BettorTable]
                .filter(en => en.id === b.id)
                .update(b)
            )
          }
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

    logMessage(s"Finishing ongoing bet as ${outcome}")

    val anyOngoingBets = DataBaseIO
      .readEntities[BetSession, BetSessionTable](
        TableQuery[BetSessionTable]
      )
      .map(_.toList)

    anyOngoingBets.flatMap { l =>
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

  case class Outcome(
      outcome: String
  )
  object Outcome extends MarshallEntity[Outcome]

  def startBetRoute: Route =
    Directives.post { ctx =>
      {
        ctx.complete {
          startBet
        }
      }
    }

  def finishBetRoute = post {
    entity(as[Outcome]) { outcome =>
      complete {
        finishBet(outcome.outcome)
      }
    }
  }
}
