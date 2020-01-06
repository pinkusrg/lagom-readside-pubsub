package com.sub.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait SubService extends Service{


  def fetchUser(id:String):ServiceCall[NotUsed,String]

  override final def descriptor: Descriptor = {
    import Service._
    named("user-consumer")
      .withCalls(
        pathCall("/consumer/fetch/:id", fetchUser _)
      )
      .withAutoAcl(true)
  }

}
