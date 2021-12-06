package model

import org.json4s.{CustomSerializer, JString}

import java.time.OffsetDateTime

case object OffsetDateTimeSerializer
    extends CustomSerializer[OffsetDateTime](format =>
      (
        { case JString(s) =>
          OffsetDateTime.parse(s)
        },
        {
          { case t: OffsetDateTime =>
            JString(t.toString)
          }
        }
      )
    )
