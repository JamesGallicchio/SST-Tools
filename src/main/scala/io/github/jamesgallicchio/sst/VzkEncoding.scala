package io.github.jamesgallicchio.sst

import enumeratum.values._

import scala.collection.immutable

sealed abstract class Codepoint(val value: Byte) extends ByteEnumEntry

case object VzkEncoding extends ByteEnum[Codepoint] {
  val values: immutable.IndexedSeq[Codepoint] = findValues
  val map: Map[Byte, Codepoint] = Map(values map { c => (c.value, c) }: _*)

  implicit val int2byte: Int => Byte = _.toByte

  sealed trait VzkChar

  // 0x00 to 0x0F: whitespace chars

  // 0x10 to 0x1F: digits
  sealed trait Digit extends VzkChar
  case object z0 extends Codepoint(0x10) with Digit
  case object z1 extends Codepoint(0x11) with Digit
  case object z2 extends Codepoint(0x12) with Digit
  case object z3 extends Codepoint(0x13) with Digit
  case object z4 extends Codepoint(0x14) with Digit
  case object z5 extends Codepoint(0x15) with Digit
  case object z6 extends Codepoint(0x16) with Digit
  case object z7 extends Codepoint(0x17) with Digit
  case object z8 extends Codepoint(0x18) with Digit
  case object z9 extends Codepoint(0x19) with Digit
  case object zX extends Codepoint(0x1a) with Digit
  case object zE extends Codepoint(0x1b) with Digit

  // 0x20 to 0x2f: vowels
  sealed trait Vowel extends VzkChar
  case object A extends Codepoint(0x20) with Vowel
  case object Aa extends Codepoint(0x21) with Vowel
  case object I extends Codepoint(0x22) with Vowel
  case object U extends Codepoint(0x23) with Vowel
  case object E extends Codepoint(0x24) with Vowel
  case object Ey extends Codepoint(0x25) with Vowel
  case object O extends Codepoint(0x26) with Vowel
  case object Yy extends Codepoint(0x27) with Vowel
  case object Uu extends Codepoint(0x28) with Vowel
  case object Ii extends Codepoint(0x29) with Vowel
  case object Oo extends Codepoint(0x2a) with Vowel
  case object Ao extends Codepoint(0x2b) with Vowel
  case object Oy extends Codepoint(0x2c) with Vowel

  // 0x30 to 0x4f: consonants
  sealed trait Consonant extends VzkChar
  case object F extends Codepoint(0x30) with Consonant
  case object B extends Codepoint(0x31) with Consonant
  case object P extends Codepoint(0x32) with Consonant
  case object Y extends Codepoint(0x33) with Consonant
  case object L extends Codepoint(0x34) with Consonant
  case object R extends Codepoint(0x35) with Consonant
  case object W extends Codepoint(0x36) with Consonant
  case object M extends Codepoint(0x37) with Consonant
  case object N extends Codepoint(0x38) with Consonant
  case object H extends Codepoint(0x39) with Consonant
  case object Z extends Codepoint(0x3a) with Consonant
  case object S extends Codepoint(0x3b) with Consonant
  case object Jh extends Codepoint(0x3c) with Consonant
  case object Sh extends Codepoint(0x3d) with Consonant
  case object Ch extends Codepoint(0x3e) with Consonant
  case object J extends Codepoint(0x3f) with Consonant
  case object D extends Codepoint(0x40) with Consonant
  case object T extends Codepoint(0x41) with Consonant
  case object Ng extends Codepoint(0x42) with Consonant
  case object G extends Codepoint(0x43) with Consonant
  case object K extends Codepoint(0x44) with Consonant
  case object V extends Codepoint(0x45) with Consonant
  case object Thh extends Codepoint(0x46) with Consonant
  case object Th extends Codepoint(0x47) with Consonant

  // 0x50 to 0x5f: punctuation
  sealed trait Punctuation extends VzkChar
  case object FullStop extends Codepoint(0x50) with Punctuation
  case object PartialStop extends Codepoint(0x51) with Punctuation
  case object LeanMark extends Codepoint(0x52) with Punctuation
  case object LiterationMark extends Codepoint(0x53) with Punctuation
  case object IndefinitiveMark extends Codepoint(0x54) with Punctuation
  case object DefinitiveMark extends Codepoint(0x55) with Punctuation

  // 0x60+: miscellaneous characters
}
