package com.pub.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.pub.api.PubService
import com.pub.impl.eventsourcing.UserEntity
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

class PubServiceLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication = {
    new PubServiceApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new PubServiceApplication(context) with LagomDevModeComponents
}
abstract class PubServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  override def jsonSerializerRegistry: JsonSerializerRegistry = UserSerializerRegistry

  //  override def lagomServer: LagomServer = serverFor[PubService](wire[com.pub.impl.PubServiceImpl])
  override lazy val lagomServer = LagomServer.forService(
    bindService[PubService].to(wire[PubServiceImpl])
  )

  persistentEntityRegistry.register(wire[UserEntity])
}