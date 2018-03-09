package io.github.jamesgallicchio.sst

import javafx.application.Application
import io.github.jamesgallicchio.sst.editor.Editor

object SSTTools {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[Editor])
  }
}