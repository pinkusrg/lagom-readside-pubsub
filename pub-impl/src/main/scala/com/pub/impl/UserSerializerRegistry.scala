package com.pub.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.pub.api.User
import com.pub.impl.eventsourcing.{CreateUserCommand, GetUserCommand, UserCreated, UserState}

object UserSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[User],
    JsonSerializer[CreateUserCommand],
    JsonSerializer[GetUserCommand],
    JsonSerializer[UserCreated],
    JsonSerializer[UserState]
  )
}
