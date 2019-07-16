package io.github.gabfssilva.r2da.client.postgressql.protocol.server

import java.nio.ByteOrder
import java.nio.charset.{Charset, StandardCharsets}

import akka.util.ByteString
import io.github.gabfssilva.r2da.api.adapters.Registry
import io.github.gabfssilva.r2da.client.postgressql.PostgresSQLDriver
import io.github.gabfssilva.r2da.client.postgressql.protocol.DataType.types
import io.github.gabfssilva.r2da.client.postgressql.protocol.server.ServerMessageAdapter.{Adapter, RowDescriptionAdapter}
import io.github.gabfssilva.r2da.client.postgressql.protocol.{DataType, PostgresSQLMessageType}
import io.github.gabfssilva.r2da.utils.ByteStringUtils._
import io.github.gabfssilva.r2da.utils.TupleUtils._

import scala.util.Try

sealed abstract class ServerMessageAdapter[T <: PostgresSQLServerMessage](val forMessage: PostgresSQLMessageType) {
  def adapt: Adapter[T]

  def forType[R <: PostgresSQLServerMessage]: ServerMessageAdapter[R] = this.asInstanceOf[ServerMessageAdapter[R]]
}

object ServerMessageAdapter {
  type Adapter[T <: PostgresSQLServerMessage] = PartialFunction[ByteString, T]

  object AuthenticationAdapter extends ServerMessageAdapter[AuthenticationMessage](PostgresSQLMessageType.Authentication) {
    override def adapt: Adapter[AuthenticationMessage] = {
      case 0 +: _    => Authenticated
      case 3 +: _    => AuthenticationChallengeClearText
      case 5 +: tail => AuthenticationChallengeMD5(tail.toArray)
    }
  }

  object BackendKeyDataAdapter extends ServerMessageAdapter[BackendKeyData](PostgresSQLMessageType.BackendKeyData) {
    override def adapt: Adapter[BackendKeyData] = {
      case byteString: ByteString =>
        val (processId, secretKey, _) =
          byteString
            .readInt
            .readInt
            .flatten

        BackendKeyData(processId, secretKey)
    }
  }

  class CommandCompleteAdapter(charset: Charset) extends ServerMessageAdapter[CommandComplete](PostgresSQLMessageType.CommandComplete) {
    override def adapt: Adapter[CommandComplete] = {
      case byteString =>
        val string = byteString.decodeString(charset)
        val rows = string.lastIndexOf(" ") match {
          case -1 => 0
          case index => Try(string.substring(index).trim.toInt).getOrElse(-1)
        }

        CommandComplete(rows, string)
    }
  }

  object DataRowAdapter extends ServerMessageAdapter[DataRow](PostgresSQLMessageType.DataRow) {
    override def adapt: Adapter[DataRow] = {
      case byteString: ByteString =>
        val (numberOfColumns, tail) = byteString.readShort

        val (data, _) = (0 to numberOfColumns).foldLeft((List.empty[Option[ByteString]],  tail)) { case ((list, t), _) =>
          val columnSize +: nt = t
          val (columnData, tail) = nt.splitAt(columnSize)
          val op = if (columnSize == -1) None else Some(columnData)
          (list :+ op, tail)
        }

        DataRow(data)
    }
  }

  class RowDescriptionAdapter(charset: Charset,
                              registry: Registry) extends ServerMessageAdapter[RowDescription](PostgresSQLMessageType.RowDescription) {
    val unknown = new DataType.Unknown(registry)
    val dataTypes: Map[Int, DataType[_]] = DataType.types(registry)

    override def adapt: Adapter[RowDescription] = {
      case rowDescriptionBytes =>
        val (columns, _) = (1 to 3).foldLeft((List.empty[ColumnInfo], rowDescriptionBytes)) { case ((list, tail), _) =>
          val (name, objectId, columnNumber, dataType, dataTypeSize, modifier, format, remaining) =
            tail
              .readCStyleString(charset)
              .readInt
              .readShort
              .readInt
              .readShort
              .readInt
              .readShort
              .flatten

          val info = ColumnInfo(
            name = name,
            tableObjectId = objectId,
            columnNumber = columnNumber,
            dataType = dataTypes.getOrElse(dataType, unknown),
            dataTypeSize = dataTypeSize,
            dataTypeModifier = modifier,
            fieldFormat = format
          )

          (list :+ info, remaining)
        }

        RowDescription(columns)
    }
  }

  def adapters(driver: PostgresSQLDriver): Map[Char, ServerMessageAdapter[_]] = List(
    AuthenticationAdapter,
    BackendKeyDataAdapter,
    new CommandCompleteAdapter(driver.charset),
    new RowDescriptionAdapter(driver.charset, driver.registry),
    DataRowAdapter
  ).map { a => a.forMessage.value -> a}.toMap
}

object Sample extends App {
  implicit val bo = ByteOrder.BIG_ENDIAN

  val rowDescriptionExample: ByteString =
    ByteString
      .newBuilder
      .putBytes("name".getBytes())
      .putInt(5)
      .putShort(1)
      .putInt(1043)
      .putShort(2)
      .putInt(5)
      .putShort(0)
      .putBytes("description".getBytes())
      .putInt(5)
      .putShort(1)
      .putInt(1043)
      .putShort(2)
      .putInt(5)
      .putShort(0)
      .putBytes("occupation".getBytes())
      .putInt(5)
      .putShort(1)
      .putInt(1043)
      .putShort(2)
      .putInt(5)
      .putShort(0)
    .result()


  val b = ByteString
      .newBuilder
      .putBytes("hohoho".getBytes())
      .putInt(1)
      .putInt(2)
      .putInt(3)
      .putInt(4)
      .putInt(5)
      .result()

  println {
    b.readCStyleString()
      .readInt
      .readInt
      .readInt
      .readInt
      .readInt
      .flatten
  }


  println(new RowDescriptionAdapter(StandardCharsets.UTF_8, Registry()).adapt(rowDescriptionExample))

//  println(rowDescriptionExample.utf8String)
}
