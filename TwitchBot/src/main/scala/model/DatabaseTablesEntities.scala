package model

import slick.dbio.DBIO
import slick.lifted
import slick.jdbc.PostgresProfile.api._

object DatabaseTablesEntities {

  def tableDefinisions = List(
    DBIO.seq(lifted.TableQuery[WhiteListedDomainTable].schema.create),
    DBIO.seq(lifted.TableQuery[FilteredTermTable].schema.create),
    DBIO.seq(lifted.TableQuery[RecurringNotificationTable].schema.create),
    DBIO.seq(lifted.TableQuery[UserCommandTable].schema.create),
    DBIO.seq(lifted.TableQuery[BettorTable].schema.create),
    DBIO.seq(lifted.TableQuery[BetSessionTable].schema.create)
  )
}
