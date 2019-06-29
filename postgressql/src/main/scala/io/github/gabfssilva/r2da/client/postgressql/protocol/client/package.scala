package io.github.gabfssilva.r2da.client.postgressql.protocol

import java.nio.charset.Charset
import java.security.MessageDigest

import akka.util.ByteString
import io.github.gabfssilva.r2da.api.adapters.Registry

package object client {
  sealed abstract class PostgresSQLClientMessage(val messageType: PostgresSQLMessageType) {
    def asByteString(registry: Registry): ByteString
  }

  sealed trait CredentialsFormat

  object CredentialsFormat {
    case object MD5 extends CredentialsFormat
    case object ClearText extends CredentialsFormat
  }

  sealed abstract class Credentials(val format: CredentialsFormat) extends PostgresSQLClientMessage(PostgresSQLMessageType.Authentication)

  case class ClearTextCredentials(username: String,
                                  password: String,
                                  encoding: Charset) extends Credentials(CredentialsFormat.ClearText) {
    override def asByteString(registry: Registry): ByteString =
      ByteString
        .newBuilder
          .putByte(messageType.value.toByte)
          .putByte(0)
          .putBytes(password.getBytes(encoding))
          .putByte(0)
        .result()
  }

  case class Md5Credentials(username: String,
                            password: String,
                            encoding: Charset,
                            salt: Array[Byte]) extends Credentials(CredentialsFormat.MD5) {
    override def asByteString(registry: Registry): ByteString = {
      val md5Digest = MessageDigest.getInstance("MD5")
      md5Digest.update(password.getBytes(encoding))
      md5Digest.update(username.getBytes(encoding))
      val passUserMd5: Array[Byte] = md5Digest.digest()

      val passUserMd5HexBytes = passUserMd5.map("%02x" format _).mkString.getBytes(encoding)
      md5Digest.update(passUserMd5HexBytes)
      md5Digest.update(salt)
      val finalMd5 = md5Digest.digest()

      val finalMd5String = "md5" + finalMd5.map("%02x" format _).mkString
      val credentials = Array.concat(finalMd5String.getBytes(encoding), Array(0.toByte))

      ByteString
        .newBuilder
          .putByte(messageType.value.toByte)
          .putByte(0)
          .putBytes(credentials)
          .putByte(0)
        .result()
    }
  }

  case class Query()
}
