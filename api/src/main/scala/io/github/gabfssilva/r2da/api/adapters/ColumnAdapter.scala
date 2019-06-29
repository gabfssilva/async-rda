package io.github.gabfssilva.r2da.api.adapters

import java.nio.charset.Charset

import akka.util.ByteString

trait ColumnAdapter[T] {
  def encode(v: T): ByteString
  def decode(b: ByteString, charset : Charset): T
}

object ColumnAdapter {
  lazy val defaultAdapters: Map[Class[_ <: Any], ColumnAdapter[_ <: Any]] = Map(
    classOf[String] -> StringAdapter,
    classOf[Long] -> LongAdapter,
    classOf[Int] -> IntAdapter
  )
}
