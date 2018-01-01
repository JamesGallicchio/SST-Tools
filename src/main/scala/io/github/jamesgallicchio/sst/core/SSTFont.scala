package io.github.jamesgallicchio.sst.core

import java.awt.{Graphics, Image}
import java.awt.image.BufferedImage

import io.github.jamesgallicchio.sst.core.VzkEncoding.{Consonant, Digit, LineAlternation, Punctuation, Vowel, VzkChar}

import scala.annotation.tailrec

case class SSTFont(name: String, lineHeight: Int, vowelSpines: Map[Vowel, Seq[(Int, Int)]], consSpines: Map[Consonant, Int],
                   images: Map[VzkChar, BufferedImage], default: BufferedImage) {

  def render(g: Graphics, str: Seq[VzkChar], maxWidth: Int = -1): Unit = {

    // Loop through characters and render them, keeping track of x and y position
    @tailrec
    def rec(g: Graphics, x: Int, xMax: Int, y: Int, yIncr: Int, seq: Seq[VzkChar]): Unit = if (seq.nonEmpty) {

      val img = getImage(seq.head)
      val nextX = x + img.getWidth

      // Check if went over line width, go to next line if yes
      if (nextX > xMax) rec(g, 0, xMax, y + yIncr, yIncr, seq)
      else {

        // Render head
        g.drawImage(img, x, y, null)

        // Check for extras & grab remaining elements
        val remaining = seq.head match {
          case v: Vowel =>
            // Grab left and right consonants to draw over vowel
            val (lefts, rest) = seq.tail.span(_.isInstanceOf[Consonant])
            val (rights, rest2) = rest.head match {
              case LineAlternation => rest.tail.span(_.isInstanceOf[Consonant])
              case _ => (Seq.empty, rest)
            }
            renderSyllable(g, x, y, v, lefts.collect { case c: Consonant => c }, rights.collect { case c: Consonant => c })
            rest2
          case _ => seq.tail
        }

        rec(g, nextX, xMax, y, yIncr, remaining)
      }
    }

    rec(g, 0, maxWidth, 0, lineHeight, str)
  }

  private def renderSyllable(g: Graphics, xOff: Int, yOff: Int,
                     vowel: Vowel, left: Seq[Consonant], right: Seq[Consonant]): Unit = {

    val vowelImg = getImage(vowel)

    g.drawImage(vowelImg, xOff, yOff, null)

    left.foldLeft(xOff){ (x, e) =>
      val img = getImage(e)
      val es = getSpineAt(e)
      val nextX = x + img.getWidth
      val vs = (getSpineAt(vowel, x) + getSpineAt(vowel, nextX))/2

      g.drawImage(img, x, vs - es, null)

      nextX
    }
    right.foldRight(xOff + vowelImg.getWidth){ (e, x) =>
      val img = getImage(e)
      val es = getSpineAt(e)
      val nextX = x - img.getWidth
      val vs = (getSpineAt(vowel, x) + getSpineAt(vowel, nextX))/2

      g.drawImage(img, x, vs - es, null)

      nextX
    }
  }

  def getImage(vch: VzkChar): BufferedImage = images.getOrElse(vch, default)

  // Gets spine height at some x value within the image
  private def getSpineAt(vow: Vowel, x: Int): Int = vowelSpines.get(vow).flatMap { spine =>

      // Find the defined points on the left and right of the specified x
      def consume(data: Seq[(Int, Int)], target: Int): Option[Int] = data match {

        // If the data is empty, no well defined answer
        case e if e.isEmpty => None

        // Only one element, so take that element's value
        case Seq(e) => Some(e._2)

        // Found the containing points, so calculate value at target on line between points
        case seq if seq.head._1 <= target && target <= seq.tail.head._1 =>
          val (x1, y1) = seq.head
          val (x2, y2) = seq.tail.head
          Some(
            //   slope      *   delta x   + y(init)
            (y1-y2)/(x1-x2) * (target-x1) + y1
          )

        case seq => consume(seq.tail, target)
      }
      consume(spine, x)
    }.getOrElse(0)

  private def getSpineAt(cons: Consonant): Int = consSpines.getOrElse(cons, 0)

  def copy(name: String = this.name, lineHeight: Int = this.lineHeight,
                          vowelSpines: Map[Vowel, Seq[(Int, Int)]] = this.vowelSpines, consSpines: Map[Consonant, Int] = this.consSpines,
                          images: Map[VzkChar, BufferedImage] = this.images, default: BufferedImage = this.default
    ): SSTFont = SSTFont(name, lineHeight, vowelSpines, consSpines, images, default)
}