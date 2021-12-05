package model

import slick.dbio.DBIO
import slick.lifted
import slick.jdbc.PostgresProfile.api._

object DatabaseTablesEntities {

  val entitiesSchemas = DBIO.seq(
    lifted.TableQuery[WhiteListedDomainTable].schema.create,
    lifted.TableQuery[RecurringNotificationTable].schema.create
  )
}
