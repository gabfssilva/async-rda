package io.github.gabfssilva.r2da.api.adapters

object Registry {
  def apply(): Registry = new Registry(ColumnAdapter.defaultAdapters)
}

class Registry(val adapters: Map[Class[_ <: Any], ColumnAdapter[_ <: Any]]) {
  def withAdapter[T](clazz: Class[T], adapter: ColumnAdapter[T]): Registry = (clazz, adapter) match {
    case (c: Class[Any], a: ColumnAdapter[Any]) => new Registry(adapters updated (c, a))
  }
}
