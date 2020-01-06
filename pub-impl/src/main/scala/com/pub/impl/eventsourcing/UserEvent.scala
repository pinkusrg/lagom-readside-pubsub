package com.pub.impl.eventsourcing

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger}
import com.pub.api.User
import play.api.libs.json.{Format, Json}

sealed trait UserEvent extends AggregateEvent[UserEvent] {
  override def aggregateTag: AggregateEventTagger[UserEvent] = UserEvent.Tag
}

object UserEvent {
  //  val numberOfShards = 4
  //  val Tag: AggregateEventShards[com.pub.impl.eventsourcing.UserEvent] = AggregateEventTag.sharded[com.pub.impl.eventsourcing.UserEvent](numberOfShards)
  val Tag = AggregateEventTag[UserEvent]
}

case class UserCreated(user: User) extends UserEvent

object UserCreated {
  implicit val format: Format[UserCreated] = Json.format
}

