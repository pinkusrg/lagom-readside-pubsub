package com.pub.api

import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

trait PubService extends Service{
  def createUser:ServiceCall[User,String]

  def userPublish():Topic[User]

  override final def descriptor: Descriptor = {
    import com.lightbend.lagom.scaladsl.api.Service._

    named("user-producer")
      .withCalls(
        pathCall("/producer/create",createUser _)
      )
      .withTopics(
        topic(PubService.TOPIC_NAME,userPublish)
      )
      .withAutoAcl(true)
  }

}
object PubService{
  val TOPIC_NAME: String = "usertopic"
}
case class User(id:String, name:String)
object User{
  implicit val format: Format[User] = Json.format[User]
}
