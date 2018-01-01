package io.github.jamesgallicchio.sst.core

import scala.util.Try

case object VzkEncoding {

  val map: Map[Byte, VzkChar] = Seq(

    // 0x00 to 0x0F: whitespace chars
    0x0a -> LineAlternation,

    // 0x10 to 0x1F: digits
    0x10 -> z0, 0x11 -> z1, 0x12 -> z2, 0x13 -> z3,
    0x14 -> z4, 0x15 -> z5, 0x16 -> z6, 0x17 -> z7,
    0x18 -> z8, 0x19 -> z9, 0x1a -> zX, 0x1b -> zE,

    // 0x20 to 0x2f: vowels
    0x20 -> A,  0x21 -> Aa, 0x22 -> I,  0x23 -> U,
    0x24 -> E,  0x25 -> Ey, 0x26 -> O,  0x27 -> Yy,
    0x28 -> Uu, 0x29 -> Ii, 0x2a -> Oo, 0x2b -> Ao,
    0x2c -> Oy,

    // 0x30 to 0x4f: consonants
    0x30 -> F, 0x31 -> B, 0x32 -> P, 0x33 -> Y,
    0x34 -> L, 0x35 -> R, 0x36 -> W, 0x37 -> M,
    0x38 -> N, 0x39 -> H, 0x3a -> Z, 0x3b -> S,
    0x3c -> Jh, 0x3d -> Sh, 0x3e -> Ch, 0x3f -> J,
    0x40 -> D, 0x41 -> T, 0x42 -> Ng, 0x43 -> G,
    0x44 -> K, 0x45 -> V, 0x46 -> Thh, 0x47 -> Th,

    // 0x50 to 0x5f: punctuation
    0x50 -> FullStop,
    0x51 -> PartialStop,
    0x52 -> LeanMark,
    0x53 -> LiterationMark,
    0x54 -> IndefinitiveMark,
    0x55 -> DefinitiveMark
  ).map(e => (e._1.toByte, e._2)).toMap

  val codepoints: Map[VzkChar, Byte] = map.foldLeft(Map.empty[VzkChar, Byte]){ (map, entry) => map + (entry._2 -> entry._1) }

  val chars: Set[VzkChar] = codepoints.keySet
  val whitespace: Set[Whitespace] = chars.collect { case c: Whitespace => c }
  val digits: Set[Digit] = chars.collect { case c: Digit => c }
  val vowels: Set[Vowel] = chars.collect { case c: Vowel => c }
  val consonants: Set[Consonant] = chars.collect { case c: Consonant => c }
  val punctuation: Set[Punctuation] = chars.collect { case c: Punctuation => c }

  def interp(seq: Seq[Byte], ignoreInvalid: Boolean = false): Try[Seq[VzkChar]] = {
    Try(
      seq.map(map.get) map {
        case Some(v) => v
        case None => throw new IllegalArgumentException("Codepoint sequence contains invalid codepoint!")
      }
    )
  }

  sealed abstract class VzkChar(val name: String)

  sealed abstract class Whitespace(name: String) extends VzkChar(name)
  case object LineAlternation extends Whitespace("LineAlternation")

  sealed abstract class Digit(name: String) extends VzkChar(name)
  case object z0 extends Digit("z0")
  case object z1 extends Digit("z1")
  case object z2 extends Digit("z2")
  case object z3 extends Digit("z3")
  case object z4 extends Digit("z4")
  case object z5 extends Digit("z5")
  case object z6 extends Digit("z6")
  case object z7 extends Digit("z7")
  case object z8 extends Digit("z8")
  case object z9 extends Digit("z9")
  case object zX extends Digit("zX")
  case object zE extends Digit("zE")

  sealed abstract class Vowel(name: String) extends VzkChar(name)
  case object A extends Vowel("A")
  case object Aa extends Vowel("Aa")
  case object I extends Vowel("I")
  case object U extends Vowel("U")
  case object E extends Vowel("E")
  case object Ey extends Vowel("Ey")
  case object O extends Vowel("O")
  case object Yy extends Vowel("Yy")
  case object Uu extends Vowel("Uu")
  case object Ii extends Vowel("Ii")
  case object Oo extends Vowel("Oo")
  case object Ao extends Vowel("Ao")
  case object Oy extends Vowel("Oy")

  sealed abstract class Consonant(name: String) extends VzkChar(name)
  case object F extends Consonant("F")
  case object B extends Consonant("B")
  case object P extends Consonant("P")
  case object N extends Consonant("N")
  case object Y extends Consonant("Y")
  case object R extends Consonant("R")
  case object W extends Consonant("W")
  case object L extends Consonant("L")
  case object M extends Consonant("M")
  case object S extends Consonant("S")
  case object H extends Consonant("H")
  case object Z extends Consonant("Z")
  case object Jh extends Consonant("Jh")
  case object Sh extends Consonant("Sh")
  case object Ch extends Consonant("Ch")
  case object J extends Consonant("J")
  case object D extends Consonant("D")
  case object T extends Consonant("T")
  case object Ng extends Consonant("Ng")
  case object G extends Consonant("G")
  case object K extends Consonant("K")
  case object V extends Consonant("V")
  case object Thh extends Consonant("Thh")
  case object Th extends Consonant("Th")

  sealed abstract class Punctuation(name: String) extends VzkChar(name)
  case object FullStop extends Punctuation("FullStop")
  case object PartialStop extends Punctuation("PartialStop")
  case object LeanMark extends Punctuation("LeanMark")
  case object LiterationMark extends Punctuation("LiterationMark")
  case object IndefinitiveMark extends Punctuation("IndefinitiveMark")
  case object DefinitiveMark extends Punctuation("DefinitiveMark")
}
