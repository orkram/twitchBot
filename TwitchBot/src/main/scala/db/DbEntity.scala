package db

import scala.reflect.runtime.universe._

@Deprecated
trait DbEntity extends Product

@Deprecated
trait DbTemplate {
  val fields: String

  def fieldsFromParams[T: TypeTag]: String = //TODO test
    typeOf[T].members
      .collect {
        case m: MethodSymbol if m.isCaseAccessor => m
      }
      .toList
      .reverse
      .map(_.toString.stripPrefix("value "))
      .mkString(" ,")
}
