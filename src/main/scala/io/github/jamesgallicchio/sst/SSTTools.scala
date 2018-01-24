package io.github.jamesgallicchio.sst

import java.awt.{BorderLayout, Color, Graphics}
import java.io.File
import javax.swing.{JFrame, JPanel, WindowConstants}

import io.github.jamesgallicchio.sst.core.FontArchiver
import io.github.jamesgallicchio.sst.core.VzkEncoding._

object SSTTools {
  def main(args: Array[String]): Unit = {

    val sequence = Seq(E,N,N,N,LineAlternation,N,N,N)

    val file = new File("/home/james/Downloads/Kyertey.sst")
    val font = FontArchiver.read(file)

    val pane = new JPanel() {
      override def paintComponent(graphics: Graphics): Unit = {
        super.paintComponent(graphics)
        font.render(graphics, sequence, this.getWidth)
      }
    }

    val frame = new JFrame("RenderTest")
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setSize(2000,2000)
    frame.setVisible(true)
    frame.add(pane)
  }
}