package com.sub.impl.readside

import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}
import com.sub.impl.eventsourcing.{MessageEvent, MessageSaved}

import scala.concurrent.ExecutionContext

class MessageProcessor(cassandraSession: CassandraSession,
                       readSide: CassandraReadSide)
                      (implicit val ec: ExecutionContext) extends ReadSideProcessor[MessageEvent] {

  val messageRepository = new MessageRepository(cassandraSession)

  override def buildHandler(): ReadSideHandler[MessageEvent] = {
    readSide.builder[MessageEvent]("messageEventOffset")
      .setGlobalPrepare(messageRepository.createTable)
      .setPrepare(_ => messageRepository.createPreparedStatements)
      .setEventHandler[MessageSaved](messageRepository.storeMessage)
      .build()
  }
//MessageEvent.Tag.allTags didn't work
  override def aggregateTags: Set[AggregateEventTag[MessageEvent]] = Set(MessageEvent.INSTANCE)
}
