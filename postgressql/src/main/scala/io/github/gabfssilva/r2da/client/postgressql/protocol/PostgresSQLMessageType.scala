package io.github.gabfssilva.r2da.client.postgressql.protocol

sealed abstract class PostgresSQLMessageType(val value: Char)

object PostgresSQLMessageType {
  case object Authentication extends PostgresSQLMessageType('R')
  case object BackendKeyData extends PostgresSQLMessageType('K')
  case object Bind extends PostgresSQLMessageType('B')
  case object BindComplete extends PostgresSQLMessageType('2')
  case object CommandComplete extends PostgresSQLMessageType('C')
  case object Close extends PostgresSQLMessageType('X')
  case object CloseStatementOrPortal extends PostgresSQLMessageType('C')
  case object CloseComplete extends PostgresSQLMessageType('3')
  case object DataRow extends PostgresSQLMessageType('D')
  case object Describe extends PostgresSQLMessageType('D')
  case object Error extends PostgresSQLMessageType('E')
  case object Execute extends PostgresSQLMessageType('E')
  case object EmptyQueryString extends PostgresSQLMessageType('I')
  case object NoData extends PostgresSQLMessageType('n')
  case object Notice extends PostgresSQLMessageType('N')
  case object NotificationResponse extends PostgresSQLMessageType('A')
  case object ParameterStatus extends PostgresSQLMessageType('S')
  case object Parse extends PostgresSQLMessageType('P')
  case object ParseComplete extends PostgresSQLMessageType('1')
  case object PasswordMessage extends PostgresSQLMessageType('p')
  case object PortalSuspended extends PostgresSQLMessageType('s')
  case object Query extends PostgresSQLMessageType('Q')
  case object RowDescription extends PostgresSQLMessageType('T')
  case object ReadyForQuery extends PostgresSQLMessageType('Z')
  case object Sync extends PostgresSQLMessageType('S')
}

