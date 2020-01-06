package com.pub.impl.eventsourcing

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.joda.time.LocalDateTime

class UserEntity extends PersistentEntity {
  override type Command = UserCommand[_]
  override type Event = UserEvent
  override type State = UserState

  override def initialState = UserState(None, LocalDateTime.now().toString())

  override def behavior = {
    case UserState(_, _) =>
      Actions()
        .onCommand[CreateUserCommand, Done] {
          case (CreateUserCommand(user), ctx, _) =>
            ctx.thenPersist(UserCreated(user))(_ => ctx.reply(Done))
        }
        .onEvent {
          case (UserCreated(user), _) =>
            UserState(Some(user), LocalDateTime.now().toString())
        }
  }
}
