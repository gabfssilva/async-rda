package io.github.gabfssilva.r2da

import akka.actor.{ActorSystem, Props}
import io.github.gabfssilva.r2da.api.{ConnectionManager, Driver, ResultSet, Transaction}

import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import io.github.gabfssilva.r2da.api.messages.{NewTransaction, Query, Update}
import io.github.gabfssilva.r2da.actors.ClientActor

class DefaultConnectionManager(driver: Driver, sys: ActorSystem = ActorSystem("r2da")) extends ConnectionManager {
  import sys._

  private implicit val timeout: Timeout = Timeout(driver.writeTimeout)

  private val client = sys.actorOf(ClientActor.props(driver))

  override def withinTransaction[T](f: Transaction => Future[T]): Future[T] =
    for {
      transaction <- (client ? NewTransaction).mapTo[Transaction]
      result      <- f(transaction)
    } yield result

  override def executeQuery(sql: String, parameters: Map[String, Any]): Future[ResultSet] =
    (client ? Query(sql, parameters)).mapTo[ResultSet]

  override def executeUpdate(sql: String, parameters: Map[String, Any])(implicit t: Transaction): Future[ResultSet] =
    (client ? Update(sql, parameters, t)).mapTo[ResultSet]
}
