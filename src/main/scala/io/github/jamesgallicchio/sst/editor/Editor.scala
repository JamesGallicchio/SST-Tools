package io.github.jamesgallicchio.sst.editor

import java.io.File
import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.ScrollPane
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.image.ImageView
import javafx.scene.layout._
import javafx.stage.Stage

import io.github.jamesgallicchio.sst.core.VzkEncoding._
import io.github.jamesgallicchio.sst.core.{FontArchiver, SSTFont}

class Editor extends Application {

  var file: Option[File] = None
  var seq: Seq[VzkChar] = Seq(O, V, A, Z, E, K, LineAlternation, N)

  val font: SSTFont = FontArchiver.read(new File("/home/james/Downloads/Kyertey.sst"))



  val root = new BorderPane

  val canvas = new Canvas {

    override def isResizable = true
    override def prefWidth(height: Double): Double = getWidth
    override def prefHeight(width: Double): Double = getHeight

    widthProperty.addListener(_ => draw)
    heightProperty.addListener(_ => draw)

    def draw: Unit = {
      getGraphicsContext2D.clearRect(0,0,getWidth,getHeight)
      setHeight(font.render(getGraphicsContext2D, seq, getWidth.toInt))
    }
  }

  val scroll = new ScrollPane(canvas)
  scroll.setFitToWidth(true)
  scroll.setFitToHeight(true)
  scroll.setHbarPolicy(ScrollBarPolicy.NEVER)
  canvas.widthProperty.bind(scroll.widthProperty)
  root.setCenter(scroll)

  val keyboard = new GridPane
  keyboard.setHgap(5)
  keyboard.setVgap(5)
  keyboard.prefHeightProperty().bind(root.heightProperty().multiply(0.3))
  keyboard.setMinHeight(100)
  root.setBottom(keyboard)

  val layout = Seq(
    Seq(null, z0, z1, z2, z3, z4, z5, z6, z7, z8, z9, zX, zE),
    Seq(E, null, Ey, null)
  ).map(_.zipWithIndex).zipWithIndex

  for (_ <- 0 to 16) {
    val c = new ColumnConstraints
    c.setPercentWidth(100.0/16)
    keyboard.getColumnConstraints.add(c)
  }
  for (_ <- 0 to 5) {
    val r = new RowConstraints
    r.setPercentHeight(100.0/5)
    keyboard.getRowConstraints.add(r)
  }

  val keyStyle =
    """
      |  -fx-border-style: dashed, none, solid;
      |  -fx-border-insets: 0em, 0.1em, 0.2em;
      |  -fx-border-radius: 0.2em;
      |  -fx-border-width: 0.1em, 0.1em, 0.1em;
      |  -fx-border-color: #000, #000, #555;
    """.stripMargin

  for {
    (row, rid) <- layout
    (ch, cid) <- row

    if ch != null

    img <- Option(font.getImage(ch))
    iv = new ImageView(SwingFXUtils.toFXImage(img, null))
    _= iv.setPreserveRatio(true)

    pane = new StackPane(iv)
    _= pane.setStyle(keyStyle)
    _= pane.setOnMouseClicked { _ => seq :+= ch; canvas.draw }

    _= GridPane.setRowIndex(pane, rid)
    _= GridPane.setColumnIndex(pane, cid)
  } keyboard.getChildren.add(pane)



  override def start(stage: Stage): Unit = {
    stage.setScene(new Scene(root, 2000, 1500))
    stage.setTitle("SST Editor")
    stage.show()
  }
}