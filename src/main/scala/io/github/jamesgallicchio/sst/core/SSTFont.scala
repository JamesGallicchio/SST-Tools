package io.github.jamesgallicchio.sst.core

import java.awt.Image
import java.awt.image.{BufferedImage, ImageObserver, Raster, RenderedImage}

import io.github.jamesgallicchio.sst.core.SSTFont.{ConsonantGlyph, DigitGlyph, Glyph, VowelGlyph}
import io.github.jamesgallicchio.sst.core.VzkEncoding.{Consonant, Digit, LineAlternation, Punctuation, Vowel, VzkChar}

import scala.annotation.tailrec

object SSTFont {
  sealed trait Glyph[T <: VzkChar] {
    def codepoint: Codepoint with T
    def image: BufferedImage
  }

  case class DigitGlyph(codepoint: Codepoint with Digit, image: BufferedImage) extends Glyph[Digit]
  case class VowelGlyph(codepoint: Codepoint with Vowel, image: BufferedImage, spine: Array[Int]) extends Glyph[Vowel]
  case class ConsonantGlyph(codepoint: Codepoint with Consonant, image: BufferedImage, spine: Int) extends Glyph[Consonant]
  case class PunctuationGlyph(codepoint: Codepoint with Punctuation, image: BufferedImage) extends Glyph[Punctuation]
}

case class SSTFont(name: String, lineHeight: Int, glyphs: Set[Glyph[_]], default: Glyph[_]) {
  private implicit def charToGlyph[T <: VzkChar](c: Codepoint with T): Glyph[T] = glyphs.collectFirst {
    case g: Glyph[T] if g.codepoint == c => g
  }.getOrElse(throw new IllegalStateException("This font is missing a glyph for codepoint " + c.toString))

  def render(str: Seq[VzkChar], maxWidth: Option[Int] = None): Image = {
    @tailrec
    def rec(image: BufferedImage, remaining: Seq[VzkChar], x: Int, y: Int): BufferedImage =
      remaining.head match {
        case v: Vowel =>
          val (lefts, rest) = remaining.tail.span(_.isInstanceOf[Consonant])
          val (rights, rest2) = rest.head match {
            case LineAlternation => rest.tail.span(_.isInstanceOf[Consonant])
            case _ => (Seq.empty, rest)
          }
          renderSyllable(v, lefts, rights)
      }

    rec(new BufferedImage(maxWidth.getOrElse(100), lineHeight, BufferedImage.TYPE_INT_ARGB), str, 0, 0)
  }

  def renderSyllable(vowel: VowelGlyph, left: Seq[ConsonantGlyph], right: Seq[ConsonantGlyph]): BufferedImage = {
    @tailrec
    def rec(img: BufferedImage, isLeft: Boolean, cons: Seq[ConsonantGlyph], x: Int): BufferedImage
      = if (cons.isEmpty) img else {
          val xp = if (isLeft) x + cons.head.image.getWidth else x - cons.head.image.getWidth
          val vs = (vowel.spine(x) + vowel.spine(xp))/2
          img.getGraphics.drawImage(cons.head.image, x, vs - cons.head.spine, null)
          rec(img, isLeft, cons.tail, xp)
        }

    val img = vowel.image
    rec(img, isLeft = true, left, 0)
    rec(img, isLeft = false, right, img.getWidth)

    img
  }
}
