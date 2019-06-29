package io.github.gabfssilva.r2da.parser

object SqlParser {
  def parse(sql: String, parameters: Map[String, Any]): String =
    parameters
      .foldLeft(sql) {
        case (partial, (k, v: String))=>
          partial.replaceAll(s":$k", s"'$v'")
        case (partial, (k, v)) =>
          partial.replaceAll(s":$k", s"$v")
      }
}
