package io.github.jamesgallicchio.sst

object Dozenal {
  implicit class String2Dozenal(val s: String) extends AnyVal {
    def doz: Int = Integer.parseInt(s, 12)
  }

  implicit class Int2Dozenal(val i: Int) extends AnyVal {
    def doz: String = Integer.toString(i, 12)
  }
}
