package db

import akka.stream.alpakka.slick.scaladsl._
@Deprecated
class DbConnection {
  //imagine using single session everywhere in the entire app. Must be crazy efficient
  implicit val session: SlickSession = SlickSession.forConfig("slick-postgres")
}
