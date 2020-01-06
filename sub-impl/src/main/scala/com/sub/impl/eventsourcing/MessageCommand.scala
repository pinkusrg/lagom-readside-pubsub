package com.sub.impl.eventsourcing

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.pub.api.User
import play.api.libs.json.Json

trait MessageCommand[R] extends ReplyType[R]

case class SaveNewMessage(message: User) extends MessageCommand[Done]

object SaveNewMessage {
  implicit val format = Json.format[SaveNewMessage]
}
