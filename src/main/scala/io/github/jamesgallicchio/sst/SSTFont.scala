package io.github.jamesgallicchio.sst

import java.awt.Image
import java.awt.image.BufferedImage

import io.github.jamesgallicchio.sst.SSTFont.Glyph
import io.github.jamesgallicchio.sst.VzkEncoding.{Consonant, Digit, Punctuation, Vowel, VzkChar}

object SSTFont {
  sealed trait Glyph[T <: VzkChar] {
    def codepoint: Codepoint with T
    def image: Image
  }

  case class DigitGlyph(codepoint: Codepoint with Digit, image: Image) extends Glyph[Digit]
  case class VowelGlyph(codepoint: Codepoint with Vowel, image: Image, spine: Array[Int]) extends Glyph[Vowel]
  case class ConsonantGlyph(codepoint: Codepoint with Consonant, image: Image, spine: Int) extends Glyph[Consonant]
  case class PunctuationGlyph(codepoint: Codepoint with Punctuation, image: Image) extends Glyph[Punctuation]

}

case class SSTFont(name: String, lineHeight: Int, glyphs: Set[Glyph[_]]) {

  def render(str: Seq[Codepoint]): Image = {
    str.foldLeft[BufferedImage](new BufferedImage(???, ???, BufferedImage.TYPE_INT_ARGB)){(bf, cd) =>
      bf
    }
  }
}
