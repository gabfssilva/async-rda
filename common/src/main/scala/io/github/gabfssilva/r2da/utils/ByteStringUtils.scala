package io.github.gabfssilva.r2da.utils

import java.nio.charset.{Charset, StandardCharsets}

import akka.util.ByteString

object ByteStringUtils {
  private val endOfCStyleStringChar = '\u0000'

  def readCStyleString(byteString: ByteString,
                       charset: Charset): (String, ByteString) = {
    val (result, tail)  = byteString.span(_ != endOfCStyleStringChar)
    result.decodeString(charset) -> tail
  }

  def readInt(byteString: ByteString): (Int, ByteString) = {
    val (v, tail) = byteString.splitAt(4)
    (v.toByteBuffer.getInt(0), tail)
  }

  def readShort(byteString: ByteString): (Short, ByteString) = {
    val (v, tail) = byteString.splitAt(2)
    (v.toByteBuffer.getShort(0), tail)
  }

  def readChar(byteString: ByteString): (Char, ByteString) = {
    val (v, tail) = byteString.splitAt(2)
    (v.toByteBuffer.getChar(0), tail)
  }

  def readLong(byteString: ByteString): (Long, ByteString) = {
    val (v, tail) = byteString.splitAt(8)
    (v.toByteBuffer.getLong(0), tail)
  }

  implicit class ByteStringImplicits(bs: ByteString) {
    def readInt: (Int, ByteString) = ByteStringUtils.readInt(bs)
    def readChar: (Char, ByteString) = ByteStringUtils.readChar(bs)
    def readShort: (Short, ByteString) = ByteStringUtils.readShort(bs)
    def readLong: (Long, ByteString) = ByteStringUtils.readLong(bs)
    def readCStyleString(charset: Charset = StandardCharsets.UTF_8): (String, ByteString) = ByteStringUtils.readCStyleString(bs, charset)
  }

  implicit class ByteStringTupleImplicits1[First](tuple: (First, ByteString)) {
    val (first, bs) = tuple

    def readInt: ((First, Int), ByteString) = {
      val (v, tail) = ByteStringUtils.readInt(bs)
      ((first, v), tail)
    }

    def readChar: ((First, Char), ByteString) = {
      val (v, tail) = ByteStringUtils.readChar(bs)
      ((first, v), tail)
    }

    def readShort: ((First, Short), ByteString) = {
      val (v, tail) = ByteStringUtils.readShort(bs)
      ((first, v), tail)
    }

    def readLong: ((First, Long), ByteString) = {
      val (v, tail) = ByteStringUtils.readLong(bs)
      ((first, v), tail)
    }

    def readCStyleString(charset: Charset = StandardCharsets.UTF_8): ((First, String), ByteString) = {
      val (v, tail) = ByteStringUtils.readCStyleString(bs, charset)
      ((first, v), tail)
    }
  }
}
