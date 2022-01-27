package model

import slick.jdbc.PostgresProfile.api._
import slick.lifted

object DatabaseTablesEntities {

  def tableDefinisions = List(
    DBIO.seq(lifted.TableQuery[WhiteListedDomainTable].schema.create),
    DBIO.seq(lifted.TableQuery[FilteredTermTable].schema.create),
    DBIO.seq(lifted.TableQuery[RecurringNotificationTable].schema.create),
    DBIO.seq(lifted.TableQuery[UserCommandTable].schema.create),
    DBIO.seq(lifted.TableQuery[BettorTable].schema.create),
    DBIO.seq(lifted.TableQuery[BetSessionTable].schema.create)
  )

  def tableCreateStatements = List(
    lifted.TableQuery[WhiteListedDomainTable].schema,
    lifted.TableQuery[FilteredTermTable].schema,
    lifted.TableQuery[RecurringNotificationTable].schema,
    lifted.TableQuery[UserCommandTable].schema,
    lifted.TableQuery[BettorTable].schema,
    lifted.TableQuery[BetSessionTable].schema
  )
}
