package processingFlows.common

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{Materializer, Supervision}
import akka.stream.Supervision.Stop
import akka.stream.alpakka.amqp.{
  AmqpUriConnectionProvider,
  WriteMessage,
  WriteResult
}
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import common.MessageLogger.logMessage
import customCommands.commands.NotificationCommand
import db.DataBaseIO
import model.{RecurringNotification, RecurringNotificationTable}
import rabbitmq.RmqMessageWriterFlow
import slick.jdbc.PostgresProfile.api._

import java.time.OffsetDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object OnTickFlow extends App {

  val tickFrequency = 2.second

  val connectionProvider: AmqpUriConnectionProvider = AmqpUriConnectionProvider(
    "amqp://localhost:5672"
  )

  private val supervisorStrategy: Supervision.Decider = { e =>
    logMessage(s"Flow has failed dramatically with exception: $e")
    Stop
  }

  implicit val system: ActorSystem = ActorSystem("mySystem1")
  implicit val mat: Materializer = Materializer(system)

  private val toRmq: Flow[WriteMessage, WriteResult, Future[Done]] =
    RmqMessageWriterFlow(
      "twitch-command-queue",
      connectionProvider
    ).amqpFlow

  val source = Source
    .tick(10.milliseconds, tickFrequency, ())
    .mapAsync(1) { _ =>
      DataBaseIO
        .readEntities[RecurringNotification, RecurringNotificationTable]( //todo use some caching mechanism to reduce number of requests to Db, maybe scaffeine?
          TableQuery[RecurringNotificationTable]
        )
    }
    .mapConcat { notifications =>
      notifications.filter(_.readyToExecute)
    }
    .mapAsync(1)(notification =>
      DataBaseIO
        .updateEntity[RecurringNotificationTable, RecurringNotification](
          TableQuery[RecurringNotificationTable]
            .filter(x => x.id === notification.id)
            .update(notification.copy(lastExecuted = OffsetDateTime.now))
        )
        .map(_ => NotificationCommand(notification))
    )
    .mapConcat(x => x.outputCommands.map(y => WriteMessage(ByteString(y))))
    .via(toRmq)
    .run()
}