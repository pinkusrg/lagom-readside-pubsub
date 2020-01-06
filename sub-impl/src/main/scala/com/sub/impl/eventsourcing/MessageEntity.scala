package com.sub.impl.eventsourcing

import java.time.LocalDateTime

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class MessageEntity extends PersistentEntity{
  override type Command = MessageCommand[_]
  override type Event = MessageEvent
  override type State = MessageState

  override def initialState = MessageState(None,LocalDateTime.now().toString)

  override def behavior: Behavior = {
    case MessageState(msg,_)=>Actions()
      .onCommand[SaveNewMessage,Done]{
        case (SaveNewMessage(msg),ctx,state)=>
          println("Observed new message")
          ctx.thenPersist(MessageSaved(msg)){
            msgSaved=>ctx.reply(Done)
          }
      }
      .onEvent{
        case (MessageSaved(msg),state)=>
          println("On Event message saved")
          MessageState(Some(msg),LocalDateTime.now().toString)
      }
  }
}
