package io.github.gabfssilva.r2da.api

import akka.actor.Props
import io.github.gabfssilva.r2da.api.adapters.Registry

import scala.concurrent.duration.FiniteDuration

trait Driver {
  val registry: Registry
  val connectionTimeout: FiniteDuration
  val writeTimeout: FiniteDuration
  val minConnections: Int
  val maxConnections: Int

  private [r2da] val actorProps: Props
}
