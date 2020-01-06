package com.sub.impl.eventsourcing

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import com.pub.api.User
import play.api.libs.json.Json

trait MessageEvent extends AggregateEvent[MessageEvent] {
  override def aggregateTag = MessageEvent.INSTANCE
}

object MessageEvent {
  val INSTANCE = AggregateEventTag[MessageEvent]
  val numberOfShards = 4
  val Tag: AggregateEventShards[MessageEvent] = AggregateEventTag.sharded[MessageEvent](numberOfShards)
}

case class MessageSaved(message: User) extends MessageEvent

object MessageSaved {
  implicit val format = Json.format[MessageSaved]
}