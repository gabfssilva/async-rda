package io.github.gabfssilva.r2da.client.postgressql.protocol

import java.net.InetSocketAddress
import java.time.{LocalDate, ZonedDateTime}
import java.util.Currency

import io.github.gabfssilva.r2da.api.adapters.{ColumnAdapter, Registry}

import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag

sealed abstract class DataType[T](val value: Int)(implicit ct: ClassTag[T]) {
  val registry: Registry
 
  lazy val classType: Class[T] = ct.runtimeClass.asInstanceOf[Class[T]]
}

sealed abstract class ListDataType[T](value: Int)(implicit ct: ClassTag[T])  extends DataType[T](value) {
  val registry: Registry
  
  override lazy val classType: Class[T] = ct.runtimeClass.asInstanceOf[Class[T]]
  
  lazy val adapter: ColumnAdapter[T] = registry.adapters(classType).forType[T]
}

object DataType {
  class Unknown(val registry: Registry) extends DataType[String](-1)
  class Untyped(val registry: Registry) extends DataType[String](0)

  class Boolean(val registry: Registry) extends DataType[Boolean](16)
  class BooleanArray(val registry: Registry) extends ListDataType[Boolean](1000)

  class Bigserial(val registry: Registry) extends DataType[Long](20)
  class BigserialArray(val registry: Registry) extends ListDataType[Long](1016)

  class Char(val registry: Registry) extends DataType[Char](18)
  class CharArray(val registry: Registry) extends ListDataType[Char](1002)

  class Smallint(val registry: Registry) extends DataType[Short](21)
  class SmallintArray(val registry: Registry) extends ListDataType[Short](1005)

  class Integer(val registry: Registry) extends DataType[Int](23)
  class IntegerArray(val registry: Registry) extends ListDataType[Int](1007)

  class Numeric(val registry: Registry) extends DataType[Number](1700)
  class NumericArray(val registry: Registry) extends ListDataType[Number](1231)

  class Real(val registry: Registry) extends DataType[BigDecimal](700)
  class RealArray(val registry: Registry) extends ListDataType[BigDecimal](1021)

  class Double(val registry: Registry) extends DataType[Double](701)
  class DoubleArray(val registry: Registry) extends ListDataType[Double](1022)

  class Serial(val registry: Registry) extends DataType[Int](23)

  class Bpchar(val registry: Registry) extends DataType[String](1042)
  class BpcharArray(val registry: Registry) extends ListDataType[String](1014)

  class Varchar(val registry: Registry) extends DataType[String](1043)
  class VarcharArray(val registry: Registry) extends ListDataType[String](1015)

  class Text(val registry: Registry) extends DataType[String](25)
  class TextArray(val registry: Registry) extends ListDataType[String](1009)

  class Timestamp(val registry: Registry) extends DataType[LocalDate](1114)
  class TimestampArray(val registry: Registry) extends ListDataType[LocalDate](1115)

  class TimestampWithTimezone(val registry: Registry) extends DataType[ZonedDateTime](1184)
  class TimestampWithTimezoneArray(val registry: Registry) extends ListDataType[ZonedDateTime](1185)

  class Date(val registry: Registry) extends DataType[LocalDate](1082)
  class DateArray(val registry: Registry) extends ListDataType[LocalDate](1182)

  class Time(val registry: Registry) extends DataType[LocalDate](1083)
  class TimeArray(val registry: Registry) extends ListDataType[LocalDate](1183)

  class TimeWithTimezone(val registry: Registry) extends DataType[ZonedDateTime](1266)
  class TimeWithTimezoneArray(val registry: Registry) extends ListDataType[ZonedDateTime](1270)

  class Interval(val registry: Registry) extends DataType[FiniteDuration](1186)
  class IntervalArray(val registry: Registry) extends ListDataType[FiniteDuration](1187)

  class OID(val registry: Registry) extends DataType[Long](26)
  class OIDArray(val registry: Registry) extends ListDataType[Long](1028)

  class ByteArray(val registry: Registry) extends DataType[Array[Byte]](17)
  class ByteArrayArray(val registry: Registry) extends ListDataType[Array[Byte]](1001)

  class Money(val registry: Registry) extends DataType[Currency](790)
  class MoneyArray(val registry: Registry) extends ListDataType[Currency](791)

  class Name(val registry: Registry) extends DataType[String](1003)
  class NameArray(val registry: Registry) extends ListDataType[String](1003)

  class XML(val registry: Registry) extends DataType[String](142)
  class XMLArray(val registry: Registry) extends ListDataType[String](143)

  class Inet(val registry: Registry) extends DataType[InetSocketAddress](869)
  class InetArray(val registry: Registry) extends ListDataType[InetSocketAddress](1041)

  class UUID(val registry: Registry) extends DataType[UUID](2950)
  class UUIDArray(val registry: Registry) extends ListDataType[UUID](2951)

  def types(registry: Registry): Map[Int, DataType[_ <: Any]] =
    List(
      new Untyped(registry),
      new Boolean(registry),
      new BooleanArray(registry),
      new Bigserial(registry),
      new BigserialArray(registry),
      new Char(registry),
      new CharArray(registry),
      new Smallint(registry),
      new SmallintArray(registry),
      new Integer(registry),
      new IntegerArray(registry),
      new Numeric(registry),
      new NumericArray(registry),
      new Real(registry),
      new RealArray(registry),
      new Double(registry),
      new DoubleArray(registry),
      new Serial(registry),
      new Bpchar(registry),
      new BpcharArray(registry),
      new Varchar(registry),
      new VarcharArray(registry),
      new Text(registry),
      new TextArray(registry),
      new Timestamp(registry),
      new TimestampArray(registry),
      new TimestampWithTimezone(registry),
      new TimestampWithTimezoneArray(registry),
      new Interval(registry),
      new IntervalArray(registry),
      new Boolean(registry),
      new BooleanArray(registry),
      new OID(registry),
      new OIDArray(registry),
      new ByteArray(registry),
      new ByteArrayArray(registry),
      new Money(registry),
      new MoneyArray(registry),
      new Name(registry),
      new NameArray(registry),
      new UUID(registry),
      new UUIDArray(registry),
      new XML(registry),
      new XMLArray(registry),
      new Inet(registry),
      new InetArray(registry)
    ).map { c => c.value -> c }.toMap
}
