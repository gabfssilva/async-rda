package io.github.gabfssilva.r2da.api.adapters

import java.nio.charset.Charset
import java.time.{LocalDateTime, ZonedDateTime}
import java.util.UUID

import akka.util.ByteString

trait ColumnAdapter[T] {
  def encode(v: T): ByteString
  def decode(b: ByteString, charset : Charset): T

  def forType[R]: ColumnAdapter[R] = this.asInstanceOf[ColumnAdapter[R]]
}

object ColumnAdapter {
  lazy val defaultAdapters: Map[Class[_ <: Any], ColumnAdapter[_ <: Any]] = Map(
    classOf[String] -> StringAdapter,
    classOf[Long] -> LongAdapter,
    classOf[Int] -> IntAdapter,
    classOf[Short] -> ShortAdapter,
    classOf[Float] -> FloatAdapter,
    classOf[Double] -> DoubleAdapter,
    classOf[LocalDateTime] -> LocalDateTimeAdapter,
    classOf[ZonedDateTime] -> ZonedDateTimeAdapter,
    classOf[Boolean] -> BooleanAdapter,
    classOf[Char]-> CharAdapter,
    classOf[UUID] -> UUIDAdapter,
    classOf[Array[Byte]] -> ByteArrayAdapter,
    classOf[BigDecimal] -> BigDecimalAdapter
  )
}