package io.github.gabfssilva.r2da.client.postgressql.protocol

import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.time.{Duration, LocalDate, ZonedDateTime}
import java.util.{Currency, Date, UUID}

import akka.util.ByteString
import io.github.gabfssilva.r2da.api.adapters.{ColumnAdapter, Registry}

import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag

sealed abstract class ColumnType[T](val value: Int)(implicit ct: ClassTag[T]) {
  lazy val classType: Class[T] = ct.runtimeClass.asInstanceOf[Class[T]]
}

sealed abstract class ListColumnType[T](value: Int)(implicit ct: ClassTag[T]) extends ColumnType[T](value) {
  override lazy val classType: Class[T] = ct.runtimeClass.asInstanceOf[Class[T]]
}

object ColumnType {
  case object Untyped extends ColumnType[String](0)

  case object Boolean extends ColumnType[Boolean](16)
  case object BooleanArray extends ListColumnType[Boolean](1000)

  case object Bigserial extends ColumnType[Long](20)
  case object BigserialArray extends ListColumnType[Long](1016)

  case object Char extends ColumnType[Char](18)
  case object CharArray extends ListColumnType[Char](1002)

  case object Smallint extends ColumnType[Short](21)
  case object SmallintArray extends ListColumnType[Short](1005)

  case object Integer extends ColumnType[Int](23)
  case object IntegerArray extends ListColumnType[Int](1007)

  case object Numeric extends ColumnType[Number](1700)
  case object NumericArray extends ListColumnType[Number](1231)

  case object Real extends ColumnType[BigDecimal](700)
  case object RealArray extends ListColumnType[BigDecimal](1021)

  case object Double extends ColumnType[Double](701)
  case object DoubleArray extends ListColumnType[Double](1022)

  case object Serial extends ColumnType[Int](23)

  case object Bpchar extends ColumnType[String](1042)
  case object BpcharArray extends ListColumnType[String](1014)

  case object Varchar extends ColumnType[String](1043)
  case object VarcharArray extends ListColumnType[String](1015)

  case object Text extends ColumnType[String](25)
  case object TextArray extends ListColumnType[String](1009)

  case object Timestamp extends ColumnType[LocalDate](1114)
  case object TimestampArray extends ListColumnType[LocalDate](1115)

  case object TimestampWithTimezone extends ColumnType[ZonedDateTime](1184)
  case object TimestampWithTimezoneArray extends ListColumnType[ZonedDateTime](1185)

  case object Date extends ColumnType[LocalDate](1082)
  case object DateArray extends ListColumnType[LocalDate](1182)

  case object Time extends ColumnType[LocalDate](1083)
  case object TimeArray extends ListColumnType[LocalDate](1183)

  case object TimeWithTimezone extends ColumnType[ZonedDateTime](1266)
  case object TimeWithTimezoneArray extends ListColumnType[ZonedDateTime](1270)

  case object Interval extends ColumnType[FiniteDuration](1186)
  case object IntervalArray extends ListColumnType[FiniteDuration](1187)

  case object OID extends ColumnType[Long](26)
  case object OIDArray extends ListColumnType[Long](1028)

  case object ByteArray extends ColumnType[Array[Byte]](17)
  case object ByteArrayArray extends ListColumnType[Array[Byte]](1001)

  case object Money extends ColumnType[Currency](790)
  case object MoneyArray extends ListColumnType[Currency](791)

  case object Name extends ColumnType[String](1003)
  case object NameArray extends ListColumnType[String](1003)

  case object XML extends ColumnType[String](142)
  case object XMLArray extends ListColumnType[String](143)

  case object Inet extends ColumnType[InetSocketAddress](869)
  case object InetArray extends ListColumnType[InetSocketAddress](1041)

  case object UUID extends ColumnType[UUID](2950)
  case object UUIDArray extends ListColumnType[UUID](2951)

  val types: Map[Int, _ >: ColumnType[Any]] =
    List(
      Untyped,
      Boolean,
      BooleanArray,
      Bigserial,
      BigserialArray,
      Char,
      CharArray,
      Smallint,
      SmallintArray,
      Integer,
      IntegerArray,
      Numeric,
      NumericArray,
      Real,
      RealArray,
      Double,
      DoubleArray,
      Serial,
      Bpchar,
      BpcharArray,
      Varchar,
      VarcharArray,
      Text,
      TextArray,
      Timestamp,
      TimestampArray,
      TimestampWithTimezone,
      TimestampWithTimezoneArray,
      Interval,
      IntervalArray,
      Boolean,
      BooleanArray,
      OID,
      OIDArray,
      ByteArray,
      ByteArrayArray,
      Money,
      MoneyArray,
      Name,
      NameArray,
      UUID,
      UUIDArray,
      XML,
      XMLArray,
      Inet,
      InetArray
    ).map { c => c.value -> c }.toMap

  def columnTypeFor(code: Int): Option[_ >: ColumnType[Any]] = types.get(code)
}
