package io.github.jamesgallicchio.sst.core

import java.awt.image.BufferedImage
import java.io._
import java.util.zip.{ZipEntry, ZipFile, ZipOutputStream}
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javax.imageio.ImageIO

import io.github.jamesgallicchio.sst.core.VzkEncoding._

import scala.collection.JavaConverters._
import scala.util.matching.Regex

/*
SST Font Format
FileName.sst: ZIP
|- config: TEXT (UTF-8)
|    > name=Font Name
|    > line-height=XX
|    > a=BB:XX,YY;XX,YY;XX,YY
|    > p=BB
|    > ...
|
|- aa.*: IMAGE
|- p.*: IMAGE
|- ...
|- default.*: IMAGE
 */

object FontArchiver {

  val configLine: Regex = """(.+)=(.+)""".r
  val vowel: Regex = """(\d+)(?:\:([0-9,;]*))?""".r
  val vowelSpine: Regex = """(\d+),(\d+)""".r
  val imageFile: Regex = """(.+)\..+""".r

  def read(archive: File): SSTFont = {
    val zip: ZipFile = new ZipFile(archive)

    zip.entries.asScala.foldLeft(
      SSTFont("", 0, 0, Map.empty[Vowel, Seq[(Int, Int)]], Map.empty[VzkChar, Int], Map.empty[VzkChar, Image], null)
    ) { (font: SSTFont, entry: ZipEntry) =>

      entry.getName.replaceAll(".*/", "") match {
        // Config file, handle as such
        case "config" =>
          // Read in lines of the file
          new BufferedReader(new InputStreamReader(zip.getInputStream(entry))).lines().iterator().asScala
            // For each line
            .foldLeft(font) { (font: SSTFont, line: String) =>

              // Attempt to match the line to key=value pattern
              val res = line match {
                case configLine(key, value) =>
                  // Match the key to possible known keys
                  key match {
                    // Configures font name
                    case "name" => Some(font.copy(name = value))
                    // Configures font line height
                    case "line-height" => Some(font.copy(lineHeight = value.toInt))
                    case "padding" => Some(font.copy(padding = value.toInt))

                    // Something else- try finding a matching character (or else None)
                    case other => VzkEncoding.chars.find(_.name.equalsIgnoreCase(other)).map {
                      // Matches vowel, so add to font vowelSpines
                      case v: Vowel =>
                        // Process int:int,int;int,int;... into Int, Seq[(Int, Int)]
                        vowel.findFirstMatchIn(value).map { m =>
                          // Base
                          (m.group(1).toInt,
                            // Spines
                            Option(m.group(2)).getOrElse("").split(";").toSeq.flatMap(vowelSpine.findFirstMatchIn)
                              .map { p => (p.group(1).toInt, p.group(2).toInt) })
                        }.map { case (base, spines) =>
                          font.copy(vowelSpines = font.vowelSpines + (v -> spines), bases = font.bases + (v -> base))
                        }.getOrElse(font)

                      // Matches anything else, so add to bases
                      case c => font.copy(bases = font.bases + (c -> value.toInt))
                    }
                  }
                // Line didn't match key=value
                case _ => None
              }

              // If the line never got matched properly to anything, throw exception
              res.getOrElse(throw new Exception("Unknown symbol in config file: " + line))
            }

        // Character image file
        case imageFile(iName) => val image = new Image(zip.getInputStream(entry))
          // Default image handling
          if (iName equalsIgnoreCase "default")
            font.copy(default = image)
          // Otherwise try to find corresponding character, and add the pair to the image map
          else
            VzkEncoding.chars.find(_.name.equalsIgnoreCase(iName)).map { char =>
              font.copy(images = font.images + (char -> image))
            }.getOrElse(font)

        case _ => font
      }
    }
  }

  def write(archive: File, font: SSTFont): Unit = {
    val zip = new ZipOutputStream(new FileOutputStream(archive))
    zip.setMethod(ZipOutputStream.DEFLATED)

    val out = new BufferedOutputStream(zip)

    zip.putNextEntry(new ZipEntry("config"))
    out.write((
        s"name=${font.name}\n" +
        s"line-height=${font.lineHeight}\n" +

        font.bases.map { // char=BB
          // for vowels, add the :XX,YY;XX,YY...
          case (v: Vowel, s) => s"${v.name}=$s:" + font.vowelSpines.getOrElse(v, Seq.empty).map { case (x, y) => s"$x,$y" }.mkString(";")
          case (c, s) => s"${c.name}=$s"
        }

      ).getBytes)
    zip.closeEntry()

    font.images.foreach { case (char, image) =>
      zip.putNextEntry(new ZipEntry(s"${char.name}.png"))
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", out)
      zip.closeEntry()
    }

    zip.finish()
    zip.close()
  }
}
