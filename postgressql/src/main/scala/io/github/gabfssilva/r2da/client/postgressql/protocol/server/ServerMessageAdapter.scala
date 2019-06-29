package io.github.gabfssilva.r2da.client.postgressql.protocol.server

import java.nio.charset.Charset

import akka.util.ByteString
import io.github.gabfssilva.r2da.client.postgressql.PostgresSQLDriver
import io.github.gabfssilva.r2da.client.postgressql.protocol.PostgresSQLMessageType
import io.github.gabfssilva.r2da.client.postgressql.protocol.server.ServerMessageAdapter.Adapter

import scala.util.{Success, Try}

sealed abstract class ServerMessageAdapter[T <: PostgresSQLServerMessage](val forMessage: PostgresSQLMessageType) {
  def adapt: Adapter[T]
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

  object BackendKeyDataParser extends ServerMessageAdapter[BackendKeyData](PostgresSQLMessageType.BackendKeyData) {
    override def adapt: Adapter[BackendKeyData] = {
      case first +: second +: _ => BackendKeyData(first, second)
    }
  }

  class CommandCompleteParser(charset: Charset) extends ServerMessageAdapter[CommandComplete](PostgresSQLMessageType.CommandComplete) {
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

  object DataRow extends ServerMessageAdapter[DataRow](PostgresSQLMessageType.DataRow) {
    override def adapt: Adapter[DataRow] = {
      case (columnSize: Short) +: tail => columnSize
    }
  }

  def adapters(driver: PostgresSQLDriver): Map[Char, ServerMessageAdapter[_]] = List(
    AuthenticationAdapter,
    BackendKeyDataParser,
    new CommandCompleteParser(driver.charset)
  ).map { a => a.forMessage.value -> a}.toMap
}
