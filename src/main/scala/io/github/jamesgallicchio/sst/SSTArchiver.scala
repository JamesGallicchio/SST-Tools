package io.github.jamesgallicchio.sst

import javax.imageio.ImageIO
import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.util.regex.Pattern
import java.util.zip.{ZipEntry, ZipFile, ZipOutputStream}

import io.github.jamesgallicchio.sst.SSTFont._
import io.github.jamesgallicchio.sst.VzkEncoding.{Consonant, Digit, Punctuation, Vowel}

import scala.collection.JavaConverters._
import scala.util.Try

object SSTArchiver {
  private val commentPattern = Pattern.compile("(.+),(\\d+)")
}

class SSTArchiver(archive: File) {

  def read(): SSTFont = {
    val zip: ZipFile = new ZipFile(archive)

    val (name, lineHeight) = {
      val m = SSTArchiver.commentPattern.matcher(zip.getComment)
      if (m.find())
        (m.group(0), Try(Integer.parseInt(m.group(1))).getOrElse(-1))
      else
        ("Unknown Font", -1)
    }

    val glyphs = zip.entries().asScala map { e =>
      VzkEncoding.values.find(_.toString == e.getName) map { codepoint =>

        val image = ImageIO.read(zip.getInputStream(e))
        codepoint match {
          case d: Digit => DigitGlyph(d, image): Glyph[_]
          case p: Punctuation => PunctuationGlyph(p, image): Glyph[_]
          case v: Vowel => VowelGlyph(v, image, e.getExtra grouped 4 map {
            case Array(a, b, c, d) => a << 24 | b << 16 | c << 8 | d
          } toArray): Glyph[_]
          case c: Consonant => ConsonantGlyph(c, image, e.getExtra grouped 4 map {
            case Array(a, b, c, d) => a << 24 | b << 16 | c << 8 | d
          } find (_ => true) getOrElse 0): Glyph[_]
        }
      }
    } filter (_.isDefined) map (_.get) toSet

    new SSTFont(name, lineHeight, glyphs)
  }

  def write(font: SSTFont): Unit = {
    val zip = new ZipOutputStream(new FileOutputStream(archive))
    zip.setComment(s"${font.name},${font.lineHeight}")
    zip.setMethod(ZipOutputStream.DEFLATED)

    val out = new BufferedOutputStream(zip)
    font.glyphs.foreach { glyph =>
      val entry = new ZipEntry(glyph.codepoint.toString)


    }
  }
}
