package io.github.jamesgallicchio.sst.editor

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

  val font: SSTFont = FontArchiver.read(new File("/home/james/Downloads/Kyertey.sst"))



  val root = new BorderPane

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

  val scroll: ScrollPane = new ScrollPane(group)
  scroll.setFitToWidth(true)
  scroll.setFitToHeight(true)
  scroll.setHbarPolicy(ScrollBarPolicy.NEVER)
  root.widthProperty().addListener(new ChangeListener[Number] {
    override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = group.updatePos
  })
  root.setCenter(scroll)

  group.updateChars


  val keyStyle =
    """
      |  -fx-border-style: dashed, none, solid;
      |  -fx-border-insets: 0em, 0.1em, 0.2em;
      |  -fx-border-radius: 0.2em;
      |  -fx-border-width: 0.1em, 0.1em, 0.1em;
      |  -fx-border-color: #000, #000, #555;
      |  
    """.stripMargin

  private def key(c: VzkChar): Node = {
    val iv = new ImageView(font.getImage(c))
    val pane = new StackPane(iv)

    iv.setPreserveRatio(true)
    iv.fitHeightProperty().bind(pane.heightProperty())

    pane.setStyle(keyStyle)
    pane.setOnMouseClicked { _ => seq :+= c; group.updateChars }

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

  val keyboard = new VBox(
    {
      val h = new HBox(
        top.map(key):_*
      )
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
      g
    },
    new HBox(
      bottom.map(key):_*
    )
  )

  root.setBottom(keyboard)

  /*
  val kb = new GridPane
  kb.setHgap(5)
  kb.setVgap(5)
  kb.prefHeightProperty().bind(root.heightProperty().multiply(0.3))
  kb.setMinHeight(300)
  kb.prefWidthProperty().bind(root.prefWidthProperty())
  kb.gridLinesVisibleProperty().set(true)
  root.setBottom(kb)


  val layout = Seq(
    Seq(null, z0, z1, z2, z3, z4, z5, z6, z7, z8, z9, zX, zE),
    //Seq(E, null, Ey, null)
  ).map(_.zipWithIndex).zipWithIndex

  for (_ <- 0 to 16) {
    val c = new ColumnConstraints
    c.prefWidthProperty().bind(kb.widthProperty().multiply(100/16.0))
    kb.getColumnConstraints.add(c)
  }
  for (_ <- 0 to 5) {
    val r = new RowConstraints
    r.setPercentHeight(100.0/5)
    kb.getRowConstraints.add(r)
  }

  for {
    (row, rid) <- layout
    (ch, cid) <- row

    if ch != null

    img <- Option(font.getImage(ch))
    iv = new ImageView(img)
    _= iv.setPreserveRatio(true)

    pane = new StackPane(iv)
    _= pane.setStyle(keyStyle)
    _= pane.setOnMouseClicked { _ => seq :+= ch; group.updateChars }

    _= GridPane.setRowIndex(pane, rid)
    _= GridPane.setColumnIndex(pane, cid)
  } kb.getChildren.add(pane) */

  override def start(stage: Stage): Unit = {
    stage.setScene(new Scene(root, 2000, 1500))
    stage.setTitle("SST Editor")
    stage.show()
  }
}