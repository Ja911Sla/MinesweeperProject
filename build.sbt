import scoverage.ScoverageKeys._

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .enablePlugins(CoverallsPlugin)
  .settings(
    name := "MinesweeperProjekt",

    // Alle Abhängigkeiten an einem Ort
    libraryDependencies ++= Seq(
      // UI
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",

      // JSON (Play-JSON für Scala 3)
      "com.typesafe.play" %% "play-json" % "2.10.0-RC9" cross CrossVersion.for3Use2_13,


// XML
      "org.scala-lang.modules" %% "scala-xml" % "2.4.0",

      // Dependency Injection (optional, falls nicht genutzt)
      "com.google.inject" % "guice" % "7.0.0",
      "net.codingwell" %% "scala-guice" % "7.0.0" cross CrossVersion.for3Use2_13,


      "org.scalatestplus" %% "mockito-4-11" % "3.2.18.0" % Test,
      // Testing
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.14" % Test

    ),

    // Testeinstellungen
    coverageEnabled := true,
    coverageFailOnMinimum := false,
    Test / fork := true,
    coverageExcludedPackages := "de\\.htwg\\.view\\.Gui"
  )
