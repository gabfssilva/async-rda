package io.github.gabfssilva.r2da.api

import io.github.gabfssilva.r2da.exceptions.ConnectionFailedException

package object messages {
  case object EstablishConnection
  case object Connected
  case object ConnectionClosed

  case class Write(data: String)

  case class ConnectionFailed(exception: ConnectionFailedException)

  trait Command

  case class Query(sql: String, parameters: Map[String, Any] = Map.empty) extends Command

  case class Update(sql: String,
                    parameters: Map[String, Any] = Map.empty,
                    transaction: Transaction = Transaction.RequireNew) extends Command

  trait DatabaseResult

  case class DatabaseResultSuccess(resultSet: ResultSet)
  case class DatabaseResultFailure(cause: Throwable)

  case object NewTransaction
}