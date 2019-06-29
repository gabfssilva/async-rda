package io.github.gabfssilva.r2da.api

import scala.concurrent.Future

trait ConnectionManager {
  def withinTransaction[T](f: Transaction => Future[T]): Future[T]
  def executeQuery(sql: String, parameters: Map[String, Any] = Map.empty): Future[ResultSet]
  def executeUpdate(sql: String, parameters: Map[String, Any] = Map.empty)(implicit t: Transaction): Future[ResultSet]
}