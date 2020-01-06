package com.pub.impl.eventsourcing

import com.pub.api.User
import play.api.libs.json.{Format, Json}

case class UserState(user: Option[User], timeStamp: String)

object UserState {
  implicit val format: Format[UserState] = Json.format
}
