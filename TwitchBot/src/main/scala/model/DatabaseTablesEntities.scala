package model

import slick.dbio.DBIO
import slick.lifted
import slick.jdbc.PostgresProfile.api._

object DatabaseTablesEntities {

  def entitiesSchemas = List(
    DBIO.seq(lifted.TableQuery[WhiteListedDomainTable].schema.create),
    DBIO.seq(lifted.TableQuery[FilteredTermTable].schema.create),
    DBIO.seq(lifted.TableQuery[RecurringNotificationTable].schema.create)
  )
}
