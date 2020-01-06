package com.pub.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRef, PersistentEntityRegistry}
import com.pub.api.{PubService, User}
import com.pub.impl.eventsourcing._
//import com.pub.api.com.pub.api.User

import scala.concurrent.ExecutionContext

class PubServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)
                  (implicit val ec: ExecutionContext) extends PubService {

  override def createUser: ServiceCall[User, String] = ServiceCall { req => {
    getRef(req.id).ask(CreateUserCommand(req)).map {
      case Done => s"com.pub.api.User ${req.name} has been created."
    }
  }
  }

  def getRef(id: String): PersistentEntityRef[UserCommand[_]] = {
    persistentEntityRegistry.refFor[UserEntity](id)
  }

  override def userPublish: Topic[User] =
    TopicProducer.singleStreamWithOffset(fromOffset =>
      persistentEntityRegistry.eventStream(UserEvent.Tag, fromOffset)
        .map(event => (convertEvent(event), event.offset)))

  private def convertEvent(
                            helloEvent: EventStreamElement[UserEvent]
                          ) = {
    helloEvent.event match {
      case UserCreated(msg) =>
        User(msg.id, msg.name)
    }
  }
}