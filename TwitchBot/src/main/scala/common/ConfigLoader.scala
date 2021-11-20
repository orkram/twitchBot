package common

import com.typesafe.config.{Config, ConfigFactory}
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.read

object ConfigLoader {

  val conf: Config = ConfigFactory.load()
  def loadConfig[A](config: Class[A])(implicit manifest: Manifest[A]): A = {
    implicit val formats: DefaultFormats = DefaultFormats

    val className = config.toString
      .split(" ")
      .flatMap(_.split(raw"\."))
      .last
      .stripSuffix("$")
      .replaceAll("""/[\W_]+|(?<=[a-z0-9])(?=[A-Z])""", "-")
      .toLowerCase()

    val s =
      conf.getConfig(className).root()

    val expr =
      raw"(?<=\().+?(?=\))".r //crappy workaround cause we get Config(Class(params)) when required Class(params)

    val jsonConfig = expr.findFirstMatchIn(s.toString) match {
      case Some(x) => x.toString()
      case _       => "fail"
    }

    read[A](jsonConfig)
  }
}
