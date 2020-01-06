import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.pub.api.PubService
import com.softwaremill.macwire._
import com.sub.api.SubService
import com.sub.impl.eventsourcing.MessageEntity
import com.sub.impl.readside.{MessageProcessor, MessageRepository}
import play.api.libs.ws.ahc.AhcWSComponents

abstract class UserConsumerApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  lazy val pubService = serviceClient.implement[PubService]
  lazy val messageRepository = wire[MessageRepository]

  override def lagomServer: LagomServer = serverFor[SubService](wire[SubServiceImpl])

  override def jsonSerializerRegistry: JsonSerializerRegistry = MessageSerializerRegistry

  persistentEntityRegistry.register(wire[MessageEntity])
  readSide.register(wire[MessageProcessor])
}

class SubServiceLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication = new UserConsumerApplication(context) {
    override def serviceLocator: ServiceLocator = NoServiceLocator
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new UserConsumerApplication(context) with LagomDevModeComponents
}