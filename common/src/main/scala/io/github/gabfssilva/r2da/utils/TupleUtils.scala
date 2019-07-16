package io.github.gabfssilva.r2da.utils

object TupleUtils {
  implicit class TupleImplicits3[T1, T2, T3](tuple: ((T1, T2), T3)) {
    def flatten: (T1, T2, T3) = {
      val ((f, s), t) = tuple
      (f, s, t)
    }
  }

  implicit class TupleImplicits4[T1, T2, T3, T4](tuple: (((T1, T2), T3), T4)) {
    def flatten: (T1, T2, T3, T4) = {
      val (((t1, t2), t3), t4) = tuple
      (t1, t2, t3, t4)
    }
  }

  implicit class TupleImplicits5[T1, T2, T3, T4, T5](tuple: ((((T1, T2), T3), T4), T5)) {
    def flatten: (T1, T2, T3, T4, T5) = {
      val ((((t1, t2), t3), t4), t5) = tuple
      (t1, t2, t3, t4, t5)
    }
  }

  implicit class TupleImplicits6[T1, T2, T3, T4, T5, T6](tuple: (((((T1, T2), T3), T4), T5), T6)) {
    def flatten: (T1, T2, T3, T4, T5, T6) = {
      val (((((t1, t2), t3), t4), t5), t6) = tuple
      (t1, t2, t3, t4, t5, t6)
    }
  }

  implicit class TupleImplicits7[T1, T2, T3, T4, T5, T6, T7](tuple: ((((((T1, T2), T3), T4), T5), T6), T7)) {
    def flatten: (T1, T2, T3, T4, T5, T6, T7) = {
      val ((((((t1, t2), t3), t4), t5), t6), t7) = tuple
      (t1, t2, t3, t4, t5, t6, t7)
    }
  }

  implicit class TupleImplicits8[T1, T2, T3, T4, T5, T6, T7, T8](tuple: (((((((T1, T2), T3), T4), T5), T6), T7), T8)) {
    def flatten: (T1, T2, T3, T4, T5, T6, T7, T8) = {
      val (((((((t1, t2), t3), t4), t5), t6), t7), t8) = tuple
      (t1, t2, t3, t4, t5, t6, t7, t8)
    }
  }

  implicit class TupleImplicits9[T1, T2, T3, T4, T5, T6, T7, T8, T9](tuple: ((((((((T1, T2), T3), T4), T5), T6), T7), T8), T9)) {
    def flatten: (T1, T2, T3, T4, T5, T6, T7, T8, T9) = {
      val ((((((((t1, t2), t3), t4), t5), t6), t7), t8), t9) = tuple
      (t1, t2, t3, t4, t5, t6, t7, t8, t9)
    }
  }

  implicit class TupleImplicits10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10](tuple: (((((((((T1, T2), T3), T4), T5), T6), T7), T8), T9), T10)) {
    def flatten: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) = {
      val (((((((((t1, t2), t3), t4), t5), t6), t7), t8), t9), t10) = tuple
      (t1, t2, t3, t4, t5, t6, t7, t8, t9, t10)
    }
  }
}
