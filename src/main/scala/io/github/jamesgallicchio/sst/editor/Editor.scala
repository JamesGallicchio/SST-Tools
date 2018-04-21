package io.github.jamesgallicchio.sst.editor

import java.awt.{Dimension, Toolkit}
import java.io.File

import javafx.application.Application
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.{Group, Node, Scene}
import javafx.scene.control.ScrollPane
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.image.ImageView
import javafx.scene.layout._
import javafx.stage.Stage
import io.github.jamesgallicchio.sst.core.VzkEncoding._
import io.github.jamesgallicchio.sst.core.{FontArchiver, SSTFont}

class Editor extends Application {

  // File for
  var file: Option[File] = None
  var seq: Seq[VzkChar] = Seq(O, V, LeanMark)//, A, Z, E, K, LineAlternation, N)

  val font: SSTFont = FontArchiver.read(new File("Kyertey.sst"))
  val style: String = getClass.getResource("/editor.css").toExternalForm
  val screenSize: Dimension = Toolkit.getDefaultToolkit.getScreenSize

  val root = new BorderPane
  root.getStylesheets.add(style)

  /*
  val group = new Group {
    private var charImages: Seq[ImageView] = Seq()

    def updateChars: Unit = {
      getChildren.clear()

      charImages = for ((img, x, y) <- font.render(seq, root.getWidth.toInt)) yield {
        val iv = new ImageView(img)
        iv.setX(x)
        iv.setY(y)
        getChildren.add(iv)
        iv
      }
    }

    def updatePos: Unit = for (((_, x, y), iv) <- font.render(seq, root.getWidth.toInt).zip(charImages)) {
      iv.setX(x)
      iv.setY(y)
    }
  }
  group.getStyleClass.add("group")
  group.updateChars

  val scroll: ScrollPane = new ScrollPane(group)
  scroll.getStyleClass.add("scroll")

  root.widthProperty().addListener(new ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = group.updatePos
  })
  root.setCenter(scroll)
  */

  private def key(c: VzkChar): StackPane = {
    val iv = new ImageView(font.getImage(c))
    val pane = new StackPane(iv)

    iv.getStyleClass.add("keyImg")
    iv.setPreserveRatio(true)
    iv.fitHeightProperty().bind(pane.heightProperty())
    iv.fitWidthProperty().bind(pane.widthProperty())

    pane.getStyleClass.add("key")
    pane.setMinSize(0, 0)
    //pane.setOnMouseClicked { _ => seq :+= c; group.updateChars }

    pane
  }


  private val top =
    Seq(z0, z1, z2, z3, z4, z5, z6, z7, z8, z9, zX, zE)
  private val middle = Seq(
    Seq(A, O, G, P, D, Aa, Oo),
    Seq(E, U, F, Th, Thh, Ey, I)
  )
  private val bottom =
    Seq(LineAlternation, LeanMark)

  val h = new HBox(
    top.map(key):_*
  )
  h.setSpacing(5)
  h

  root.setCenter(h) /*

  val keyboard = new VBox({
      val h = new HBox(
        top.map(key):_*
      )
      h.setSpacing(5)
      h
    }, {
      val g = new GridPane()
      for {
        (row, rownum) <- middle.zipWithIndex
        (c, colnum) <- row.zipWithIndex
      } {
        val k = key(c)
        GridPane.setRowIndex(k, rownum)
        GridPane.setColumnIndex(k, colnum)
        g.getChildren.add(k)
      }
      g.setVgap(5)
      g.setHgap(5)
      g
    }, {
      val h = new HBox(
        bottom.map(key): _*
      )
      h
    }
  )

  keyboard.setSpacing(5)
  keyboard.prefHeightProperty.bind(root.heightProperty().multiply(0.25))
  keyboard.setMinHeight(200)

  root.setBottom(keyboard)*/

  override def start(stage: Stage): Unit = {
    stage.setScene(new Scene(root, 2000, 1500))
    stage.setTitle("SST Editor")
    stage.show()
  }
}
