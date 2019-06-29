package io.github.gabfssilva.r2da.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.{DefaultResizer, RoundRobinPool}
import io.github.gabfssilva.r2da.api.Driver
import io.github.gabfssilva.r2da.api.messages._
import io.github.gabfssilva.r2da.parser.SqlParser

object ClientActor {
  def props(driver: Driver): Props = {
    val resizer: DefaultResizer = DefaultResizer(lowerBound = driver.minConnections, upperBound = driver.maxConnections)
    val poolRouter = RoundRobinPool(driver.minConnections, Some(resizer))
    Props(new ClientActor(driver)).withRouter(poolRouter)
  }
}

class ClientActor(driver: Driver) extends Actor {
  import context._

  override def receive: Receive = disconnected

  def disconnected: Receive = {
    case c: Command =>
      val connection = system.actorOf(driver.actorProps)
      become(waitingConnection(c, sender()))
      connection ! EstablishConnection
  }

  def waitingConnection(c: Command,
                        client: ActorRef): Receive = {
    case Connected =>
      val connection = sender()
      become(available(connection, client))
      self ! c
    case ConnectionFailed(e) =>
      client ! Left(e)
      stop(self)
  }

  def available(connection: ActorRef,
                client: ActorRef): Receive = {
    case Query(sql, parameters) =>
      connection ! Write(SqlParser.parse(sql, parameters))
      become(expectingResult(connection, client))
    case Update(sql, parameters, transaction) =>
      connection ! Write(SqlParser.parse(sql, parameters))
      become(expectingResult(connection, client))
    case ConnectionClosed =>
      connection ! EstablishConnection
  }

  def expectingResult(connection: ActorRef,
                      client: ActorRef): Receive = {
    case DatabaseResultSuccess(resultSet) =>
      client ! Right(resultSet)
      become(available(connection, client))
    case DatabaseResultFailure(cause) =>
      client ! Left(cause)
      become(available(connection, client))
  }
}
