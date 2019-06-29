package io.github.gabfssilva.r2da.api

import java.time.ZonedDateTime

case class Column(index: Int, name: String, value: Option[Any]) {
  def stringValue: Option[String] = value.collect { case v: String => v }
  def longValue: Option[Long] = value.collect { case v: Long => v }
  def intValue: Option[Int] = value.collect { case v: Int => v }
  def zonedDateTimeValue: Option[ZonedDateTime] = value.collect { case v: ZonedDateTime => v }
}

case class Row(columns: List[Column]) {
  def columnByName(name: String): Option[Column] = columns.find(_.name == name)
}

case class ResultSet(rowsChanged: Int = 0,
                     rows: List[Row] = List.empty)

trait Transaction

object Transaction {
  object RequireNew extends Transaction
  object None extends Transaction
}