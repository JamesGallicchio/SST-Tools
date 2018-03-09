package io.github.jamesgallicchio.sst.core

import java.awt.Graphics
import java.awt.image.BufferedImage
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.GraphicsContext

import io.github.jamesgallicchio.sst.core.VzkEncoding.{Consonant, LineAlternation, Vowel, VzkChar}

import scala.annotation.tailrec

case class SSTFont(name: String, lineHeight: Int, padding: Int, vowelSpines: Map[Vowel, Seq[(Int, Int)]],
                   bases: Map[VzkChar, Int], images: Map[VzkChar, BufferedImage], default: BufferedImage) {

  def render(graphics: Graphics, str: Seq[VzkChar], maxWidth: Int): Int = {
    render(graphics.drawImage(_, _, _, null), str, maxWidth)
  }

  def render(graphics: GraphicsContext, str: Seq[VzkChar], maxWidth: Int): Int = {
    render({ (img, x, y) => println("drawing image"); graphics.drawImage(SwingFXUtils.toFXImage(img, null), x, y) }, str, maxWidth)
  }

  def render(drawer: (BufferedImage, Int, Int) => Unit, str: Seq[VzkChar], maxWidth: Int): Int = {

    println(s"Rendering ${str.mkString(",")} on $maxWidth wide thing")

    // Loop through characters and render them, keeping track of x and y position
    @tailrec
    def rec(draw: (BufferedImage, Int, Int) => Unit, x: Int, xMax: Int, xPad: Int, y: Int, yIncr: Int,
            seq: Seq[VzkChar]): Int = if (seq.nonEmpty) {

      println("rec")
      val img = getImage(seq.head)
      val nextX = x + img.getWidth + xPad

      // Check if went over line width, go to next line if yes
      if (xMax >= 0 && nextX > xMax && x != xPad) {
        rec(draw, xPad, xMax, xPad, y + yIncr, yIncr, seq)
      } else {

        // Calculate y value to line base up to halfway down line
        val baseY = y + yIncr/2 - getBase(seq.head)

        // Check for syllables & grab remaining elements
        val remaining = seq.head match {
          case v: Vowel =>
            // Grab left and right consonants to draw over vowel
            val (lefts, rest) = seq.tail.span(_.isInstanceOf[Consonant])
            val (rights, rest2) =
              if (rest.isEmpty) (Seq.empty, Seq.empty)
              else rest.head match {
                case LineAlternation => rest.tail.span(_.isInstanceOf[Consonant])
                case _ => (Seq.empty, rest)
              }
            renderSyllable(draw, x, baseY, v, lefts.collect { case c: Consonant => c }, rights.collect { case c: Consonant => c })
            rest2
          case _ =>
            // Render whatever else it is
            draw(img, x, baseY)
            seq.tail
        }

        rec(draw, nextX, xMax, xPad, y, yIncr, remaining)
      }
    } else y+yIncr

    rec(drawer, padding, maxWidth, padding, 0, lineHeight, str)
  }

  private def renderSyllable(draw: (BufferedImage, Int, Int) => Unit, xOff: Int, yOff: Int,
                     vowel: Vowel, left: Seq[Consonant], right: Seq[Consonant]): Unit = {

    val vowelImg = getImage(vowel)

    draw(vowelImg, xOff, yOff)

    left.foldLeft(xOff){ (x, e) =>
      val img = getImage(e)
      val es = getBase(e)
      val nextX = x + img.getWidth + padding
      val vs = getSpineAt(vowel, (x+nextX)/2)

      println(s"$x to $nextX: $e ${img.getWidth}")

      draw(img, x, yOff + vs - es)

      nextX
    }
    right.foldRight(xOff + vowelImg.getWidth){ (e, x) =>
      val img = getImage(e)
      val es = getBase(e)
      val nextX = x - img.getWidth - padding
      val vs = getSpineAt(vowel, (x+nextX)/2)

      draw(img, nextX, yOff + vs - es)

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

  def getBase(ch: VzkChar): Int = bases.getOrElse(ch, 0)

  def copy(name: String = this.name, lineHeight: Int = this.lineHeight, padding: Int = this.padding,
           vowelSpines: Map[Vowel, Seq[(Int, Int)]] = this.vowelSpines, bases: Map[VzkChar, Int] = this.bases,
           images: Map[VzkChar, BufferedImage] = this.images, default: BufferedImage = this.default
    ): SSTFont = SSTFont(name, lineHeight, padding, vowelSpines, bases, images, default)
}