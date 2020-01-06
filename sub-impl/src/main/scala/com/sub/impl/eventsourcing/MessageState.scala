package com.sub.impl.eventsourcing

import com.pub.api.User
import play.api.libs.json.{Format, Json}

case class MessageState(message: Option[User], timestamp: String)

object MessageState {
  implicit val format: Format[MessageState] = Json.format[MessageState]
}