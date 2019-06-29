package io.github.gabfssilva.r2da.api
import java.nio.charset.Charset

import akka.util.ByteString

package object adapters {
  case object StringAdapter extends ColumnAdapter[String] {
    override def encode(v: String): ByteString = ByteString(v)
    override def decode(b: ByteString, charset : Charset): String = b.decodeString(charset)
  }

  case object LongAdapter extends ColumnAdapter[Long] {
    override def encode(v: Long): ByteString = ByteString(v)
    override def decode(b: ByteString, charset : Charset): Long = b.utf8String.toLong
  }

  case object IntAdapter extends ColumnAdapter[Int] {
    override def encode(v: Int): ByteString = ByteString(v)
    override def decode(b: ByteString, charset : Charset): Int = b.utf8String.toInt
  }
}
