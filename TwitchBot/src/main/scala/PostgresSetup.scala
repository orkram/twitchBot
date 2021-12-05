import model.DatabaseTablesEntities
import slick.jdbc.PostgresProfile.api._

object PostgresSetup extends App {

  val db = Database.forConfig("slick-postgres2")

  db.run(
    DatabaseTablesEntities.entitiesSchemas
  )
}
