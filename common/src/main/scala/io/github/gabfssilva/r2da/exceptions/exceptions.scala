package io.github.gabfssilva.r2da

import java.net.InetSocketAddress

package object exceptions {
  case class ConnectionFailedException(remote: InetSocketAddress)
    extends RuntimeException(s"Error while trying to connect to ${remote.getHostString}:${remote.getPort}")

  case class AlreadyClosedException() extends RuntimeException
}
