import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.pub.api.{PubService, User}
import com.sub.api.SubService
import com.sub.impl.eventsourcing.{MessageEntity, SaveNewMessage}
import com.sub.impl.readside.MessageRepository
//import com.pub.api.User
//import com.sub.impl.eventsourcing.com.sub.impl.eventsourcing.MessageEntity

import scala.concurrent.ExecutionContext

class SubServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                     userProducerService: PubService,
                     messageRepository: MessageRepository)(implicit val ec: ExecutionContext)
  extends SubService {


  userProducerService.userPublish.subscribe.atLeastOnce {
    Flow[User].map { msg =>
      putMessage(msg)
      Done
    }
  }

  override def fetchUser(id: String): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    println("In fetchUser")
    messageRepository.getUserById(id).map(user =>
      s"User for id:${user.get.id} is ${user.get.name}")
  }

  private def putMessage(user: User) = {
    println(s"obersrve new message ${user}")
    getRef(user.id).ask(SaveNewMessage(user))
  }

  def getRef(id: String) =
    persistentEntityRegistry.refFor[MessageEntity](id)
}
