package api

import model.MarshallEntity

case class Outcome(
    outcome: String
)
object Outcome extends MarshallEntity[Outcome]
