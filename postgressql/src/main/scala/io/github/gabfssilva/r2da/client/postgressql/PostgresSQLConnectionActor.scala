package io.github.gabfssilva.r2da.client.postgressql

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp.{Close, CommandFailed, Connect, Connected, ConnectionClosed, Received, Register, Write}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import io.github.gabfssilva.r2da.api.adapters.Registry
import io.github.gabfssilva.r2da.api.messages
import io.github.gabfssilva.r2da.api.messages.EstablishConnection
import io.github.gabfssilva.r2da.exceptions.ConnectionFailedException

class PostgresSQLConnectionActor(driver: PostgresSQLDriver, listener: ActorRef) extends Actor {
  import context.system

  val registry: Registry = driver.registry

  private val remote = InetSocketAddress.createUnresolved(driver.host, driver.port)

  override def receive: Receive = {
    case EstablishConnection =>
      IO(Tcp) ! Connect(remote)

    case CommandFailed(_: Connect) =>
      sender() ! ConnectionFailedException(remote)
      context.stop(self)

    case c @ Connected(_, _) =>
      listener ! c
      val connection = sender()
      connection ! Register(self)
      context.become(connected(connection))
  }

  def connected(connection: ActorRef): Receive = {
    case messages.Write(data) =>
      connection ! Write(ByteString(data))

    case CommandFailed(w: Write) =>
      listener ! "write failed"

    case Received(data) =>
      listener ! data

    case "close" =>
      connection ! Close

    case _: ConnectionClosed =>
      listener ! "connection closed"
      context.stop(self)
  }
}
