package io.github.gabfssilva.r2da.client.postgressql.protocol

import akka.util.ByteString

package object server {
  sealed abstract class PostgresSQLServerMessage(val messageType: PostgresSQLMessageType)

  sealed abstract class AuthenticationMessage extends PostgresSQLServerMessage(PostgresSQLMessageType.Authentication)

  case object Authenticated extends AuthenticationMessage
  case object AuthenticationChallengeClearText extends AuthenticationMessage
  case class AuthenticationChallengeMD5(salt: Array[Byte]) extends AuthenticationMessage

  case class BackendKeyData(processId: Int, secretKey: Int) extends PostgresSQLServerMessage(PostgresSQLMessageType.BackendKeyData)
  case class CommandComplete(rowsAffected: Int, statusMessage: String) extends PostgresSQLServerMessage(PostgresSQLMessageType.CommandComplete)
  case class DataRow(data: List[Option[ByteString]]) extends PostgresSQLServerMessage(PostgresSQLMessageType.DataRow)

  case class ColumnInfo(name: String,
                        tableObjectId: Int,
                        columnNumber: Int,
                        dataType: DataType[_ <: Any],
                        dataTypeSize: Long,
                        dataTypeModifier: Int,
                        fieldFormat: Int
                       )

  case class RowDescription(columns: List[ColumnInfo]) extends PostgresSQLServerMessage(PostgresSQLMessageType.RowDescription)
}
