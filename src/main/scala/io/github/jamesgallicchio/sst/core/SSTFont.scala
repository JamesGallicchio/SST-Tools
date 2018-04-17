package io.github.jamesgallicchio.sst.core

import javafx.scene.image.Image

import io.github.jamesgallicchio.sst.core.VzkEncoding.{Consonant, LineAlternation, Vowel, VzkChar}

import scala.annotation.tailrec

case class SSTFont(name: String, lineHeight: Int, padding: Int, vowelSpines: Map[Vowel, Seq[(Int, Int)]],
                   bases: Map[VzkChar, Int], images: Map[VzkChar, Image], default: Image) {

  def render(str: Seq[VzkChar], maxWidth: Int): Seq[(Image, Int, Int)] = {

    println(s"Rendering ${str.mkString(",")} on $maxWidth wide thing")

    // Loop through characters and render them, keeping track of x and y position
    @tailrec
    def rec(x: Int, xMax: Int, xPad: Int, y: Int, yIncr: Int,
            seq: Seq[VzkChar], acc: Seq[(Image, Int, Int)]): Seq[(Image, Int, Int)] = if (seq.nonEmpty) {

      println("rec")
      val img = getImage(seq.head)
      val nextX = x + img.getWidth.toInt + xPad

      // Check if went over line width, go to next line if yes
      if (xMax >= 0 && nextX > xMax && x != xPad) {
        rec(xPad, xMax, xPad, y + yIncr, yIncr, seq, acc)
      } else {

        // Calculate y value to line base up to halfway down line
        val baseY = y + yIncr / 2 - getBase(seq.head)

        // Check for syllables & grab remaining elements
        val (remaining, newAcc) = seq.head match {
          case v: Vowel =>
            // Grab left and right consonants to draw over vowel
            val (lefts, rest) = seq.tail.span(_.isInstanceOf[Consonant])
            val (rights, rest2) =
              if (rest.isEmpty) (Seq.empty, Seq.empty)
              else rest.head match {
                case LineAlternation => rest.tail.span(_.isInstanceOf[Consonant])
                case _ => (Seq.empty, rest)
              }
            (rest2, renderSyllable(x, baseY, v, lefts.collect { case c: Consonant => c }, rights.collect { case c: Consonant => c }, acc))
          case _ =>
            // Render whatever else it is
            (seq.tail, (img, x, baseY) +: acc)
        }

        rec(nextX, xMax, xPad, y, yIncr, remaining, newAcc)
      }
    } else acc

    rec(padding, maxWidth, padding, 0, lineHeight, str, Seq()).reverse
  }

  private def renderSyllable(xOff: Int, yOff: Int, vowel: Vowel, left: Seq[Consonant], right: Seq[Consonant],
                             acc: Seq[(Image, Int, Int)]): Seq[(Image, Int, Int)] = {

    val vowelImg = getImage(vowel)

    val wVowel = (vowelImg, xOff, yOff) +: acc


    val (_, wLeft) = left.foldLeft((xOff, wVowel)) { case ((x, ac), e) =>
      val img = getImage(e)
      val es = getBase(e)
      val nextX = x + img.getWidth.toInt + padding
      val vs = getSpineAt(vowel, (x + nextX) / 2)

      println(s"$x to $nextX: $e ${img.getWidth}")

      (nextX, (img, x, yOff + vs - es) +: ac)
    }
    val (_, wRight) = right.foldRight((xOff + vowelImg.getWidth.toInt, wLeft)) { case (e, (x, ac)) =>
      val img = getImage(e)
      val es = getBase(e)
      val nextX = x - img.getWidth.toInt - padding
      val vs = getSpineAt(vowel, (x + nextX) / 2)

      (nextX, (img, nextX, yOff + vs - es) +: ac)
    }

    wRight
  }

  def getImage(vch: VzkChar): Image = images.getOrElse(vch, default)

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
          (y1 - y2) / (x1 - x2) * (target - x1) + y1
        )

      case seq => consume(seq.tail, target)
    }

    consume(spine, x)
  }.getOrElse(0)

  def getBase(ch: VzkChar): Int = bases.getOrElse(ch, 0)

  def copy(name: String = this.name, lineHeight: Int = this.lineHeight, padding: Int = this.padding,
           vowelSpines: Map[Vowel, Seq[(Int, Int)]] = this.vowelSpines, bases: Map[VzkChar, Int] = this.bases,
           images: Map[VzkChar, Image] = this.images, default: Image = this.default
          ): SSTFont = SSTFont(name, lineHeight, padding, vowelSpines, bases, images, default)
}