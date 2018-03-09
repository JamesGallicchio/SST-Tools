lazy val root = (project in file("."))
  .settings(
    name := "SSTTools",
    organization := "io.github.jamesgallicchio",
    version := "0.1.0",
    scalaVersion := "2.12.3",
    libraryDependencies ++= Seq(
      "com.beachape" %% "enumeratum" % "1.5.12"
    ),
    fork := true
  )