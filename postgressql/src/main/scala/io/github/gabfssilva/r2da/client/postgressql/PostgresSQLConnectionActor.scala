package io.github.gabfssilva.r2da.client.postgressql

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import io.github.gabfssilva.r2da.api.adapters.Registry
import io.github.gabfssilva.r2da.api.messages.EstablishConnection
import io.github.gabfssilva.r2da.api.{Column, Row, messages}
import io.github.gabfssilva.r2da.client.postgressql.protocol.client.{ClearTextCredentials, Md5Credentials}
import io.github.gabfssilva.r2da.client.postgressql.protocol.server._
import io.github.gabfssilva.r2da.exceptions.ConnectionFailedException

import io.github.gabfssilva.r2da.utils.ByteStringUtils._

class PostgresSQLConnectionActor(driver: PostgresSQLDriver, listener: ActorRef) extends Actor {
  import context._

  val registry: Registry = driver.registry
  val serverMessageAdapters: Map[Char, ServerMessageAdapter[_]] = driver.serverMessageAdapters

  private val remote = InetSocketAddress.createUnresolved(driver.host, driver.port)

  override def receive: Receive = {
    case EstablishConnection =>
      IO(Tcp) ! Connect(remote)

    case CommandFailed(_: Connect) =>
      sender() ! ConnectionFailedException(remote)
      stop(self)

    case c @ Connected(_, _) =>
      listener ! c
      val connection = sender()
      connection ! Register(self)
      become(available(connection))
  }

  def available(connection: ActorRef): Receive = {
    case messages.Write(data) =>
      connection ! Write(ByteString(data))

    case CommandFailed(w: Write) =>
      listener ! "write failed"

    case Received(data: ByteString) =>
      val receiveType +: size +: tail = data

      serverMessageAdapters.get(receiveType.toChar) match {
        case None =>
          listener ! Left(new RuntimeException("unexpected response type from PostgresSQL"))

        case Some(messageAdapter: ServerMessageAdapter[_]) =>
          messageAdapter.adapt(tail) match {
            case AuthenticationChallengeClearText =>
              (for {
                u <- driver.username
                p <- driver.password
              } yield (u, p)) match {
                case None =>
                  listener ! Left(new RuntimeException("Username AND password are required"))
                case Some((u, p)) =>
                  connection ! Write(ClearTextCredentials(u, p, driver.charset).asByteString)
              }

            case AuthenticationChallengeMD5(salt) =>
              (for {
                u <- driver.username
                p <- driver.password
              } yield (u, p)) match {
                case None =>
                  listener ! Left(new RuntimeException("Username AND password are required"))
                case Some((u, p)) =>
                  connection ! Write(Md5Credentials(u, p, driver.charset, salt).asByteString)
              }
          }
      }

      listener ! data

    case "close" =>
      connection ! Close

    case _: ConnectionClosed =>
      listener ! "connection closed"
      stop(self)
  }

  def waitingRowData(description: RowDescription,
                     receivedRows: List[Row] = List.empty): Receive = {
    case Received(data: ByteString) =>
      data.readInt

      val receiveType +: _ +: tail = data

      serverMessageAdapters.get(receiveType.toChar) match {
        case None =>
          listener ! Left(new RuntimeException("unexpected response type from PostgresSQL"))

        case Some(messageAdapter: ServerMessageAdapter[_]) =>
          messageAdapter.adapt(tail) match {
            case DataRow(info) =>
              val columns = (description.columns zip info).map { case (column, row) =>
                Column(
                  index = column.columnNumber,
                  name = column.name,
                  value = for {
                    r <- row
                    decoder <- registry.adapters.get(column.dataType.classType)
                  } yield decoder.decode(r, driver.charset)
                )
              }

              become(waitingRowData(description, receivedRows :+ Row(columns)))
          }
      }
  }
}
