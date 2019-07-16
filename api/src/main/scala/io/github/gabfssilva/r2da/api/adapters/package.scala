package io.github.gabfssilva.r2da.api
import java.nio.charset.Charset
import java.time.{LocalDateTime, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.UUID

import akka.util.ByteString

package object adapters {
  case object ByteArrayAdapter extends ColumnAdapter[Array[Byte]] {
    override def encode(v: Array[Byte]): ByteString = ByteString(v.toString)
    override def decode(b: ByteString, charset : Charset): Array[Byte] = b.toArray
  }

  case object StringAdapter extends ColumnAdapter[String] {
    override def encode(v: String): ByteString = ByteString(v)
    override def decode(b: ByteString, charset : Charset): String = b.decodeString(charset)
  }

  case object LongAdapter extends ColumnAdapter[Long] {
    override def encode(v: Long): ByteString = ByteString(v.toString)
    override def decode(b: ByteString, charset : Charset): Long = b.decodeString(charset).toLong
  }

  case object IntAdapter extends ColumnAdapter[Int] {
    override def encode(v: Int): ByteString = ByteString(v.toString)
    override def decode(b: ByteString, charset : Charset): Int = b.decodeString(charset).toInt
  }

  case object DoubleAdapter extends ColumnAdapter[Double] {
    override def encode(v: Double): ByteString = ByteString(v.toString)
    override def decode(b: ByteString, charset : Charset): Double = b.decodeString(charset).toDouble
  }

  case object FloatAdapter extends ColumnAdapter[Float] {
    override def encode(v: Float): ByteString = ByteString(v.toString)
    override def decode(b: ByteString, charset : Charset): Float = b.decodeString(charset).toFloat
  }

  case object ShortAdapter extends ColumnAdapter[Short] {
    override def encode(v: Short): ByteString = ByteString(v.toString)
    override def decode(b: ByteString, charset : Charset): Short = b.decodeString(charset).toShort
  }

  case object UUIDAdapter extends ColumnAdapter[UUID] {
    override def encode(v: UUID): ByteString = ByteString(v.toString)
    override def decode(b: ByteString, charset : Charset): UUID = UUID.fromString(b.utf8String)
  }

  case object CharAdapter extends ColumnAdapter[Char] {
    override def encode(v: Char): ByteString = ByteString(v.toString)
    override def decode(b: ByteString, charset : Charset): Char = {
      val char +: _ = b
      char.toChar
    }
  }

  case object BooleanAdapter extends ColumnAdapter[Boolean] {
    override def encode(v: Boolean): ByteString = ByteString(v.toString)
    override def decode(b: ByteString, charset : Charset): Boolean = b.decodeString(charset).toBoolean
  }

  case object ZonedDateTimeAdapter extends ColumnAdapter[ZonedDateTime] {
    override def encode(v: ZonedDateTime): ByteString = ByteString(v.format(DateTimeFormatter.ISO_DATE))
    override def decode(b: ByteString, charset: Charset): ZonedDateTime = ZonedDateTime.parse(b.decodeString(charset))
  }

  case object LocalDateTimeAdapter extends ColumnAdapter[LocalDateTime] {
    override def encode(v: LocalDateTime): ByteString = ByteString(v.format(DateTimeFormatter.ISO_LOCAL_TIME))
    override def decode(b: ByteString, charset: Charset): LocalDateTime = LocalDateTime.parse(b.decodeString(charset))
  }

  case object BigDecimalAdapter extends ColumnAdapter[BigDecimal] {
    override def encode(v: BigDecimal): ByteString = ByteString(v.toString())
    override def decode(b: ByteString, charset: Charset): BigDecimal = BigDecimal(b.utf8String)
  }
}
