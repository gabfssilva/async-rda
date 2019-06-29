package io.github.gabfssilva.r2da.client.postgressql

import java.nio.charset.{Charset, StandardCharsets}

import akka.actor.Props
import io.github.gabfssilva.r2da.api.Driver
import io.github.gabfssilva.r2da.api.adapters.Registry

import scala.concurrent.duration.{FiniteDuration, _}

case class PostgresSQLDriver(host: String = "localhost",
                             port: Int = 5432,
                             ssl: Boolean = false,
                             charset: Charset = StandardCharsets.UTF_8,
                             username: Option[String],
                             password: Option[String],
                             connectionTimeout: FiniteDuration = 10.seconds,
                             writeTimeout: FiniteDuration = 10.seconds,
                             maxConnections: Int = 10,
                             minConnections: Int = 2,
                             registry: Registry = Registry()) extends Driver {

  override private[r2da] val actorProps = Props(new PostgresSQLConnectionActor(this, null))
}
