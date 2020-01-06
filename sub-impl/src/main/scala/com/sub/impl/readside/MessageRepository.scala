package com.sub.impl.readside

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.lightbend.lagom.scaladsl.persistence.EventStreamElement
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.pub.api.User
import com.sub.impl.eventsourcing.MessageSaved

import scala.concurrent.{ExecutionContext, Future}

class MessageRepository(cassandraSession: CassandraSession)(implicit ec: ExecutionContext) {

  var message_preparedStatements: PreparedStatement = _

  def createTable(): Future[Done] = {
    cassandraSession.executeCreateTable(
      """
        |CREATE TABLE IF NOT EXISTS useruser(
        |id text PRIMARY KEY,
        |name text);
        |""".stripMargin
    )
  }

  def createPreparedStatements(): Future[Done] = {

    println("Creating prepared statements..")
    for {
      messagePreparedStatement <- cassandraSession.prepare("INSERT INTO useruser(id, name) VALUES (?, ?)")
    } yield {
      message_preparedStatements = messagePreparedStatement
      Done
    }
  }

  def storeMessage(eventElement: EventStreamElement[MessageSaved]): Future[List[BoundStatement]] = {

    println("Storing message..")
    val messageBindStatement = message_preparedStatements.bind()
    messageBindStatement.setString("id", eventElement.event.message.id)
    messageBindStatement.setString("name", eventElement.event.message.name)
    Future.successful(List(messageBindStatement))
  }


  def getUserById(id: String) = {
    println("Quering user..")
    cassandraSession.selectOne(s"SELECT * FROM useruser WHERE id='$id'").map { optRow =>
      optRow.map { row => {
        val id = row.getString("id")
        val name = row.getString("name")
        User(id, name)
      }
      }
    }
  }
}
