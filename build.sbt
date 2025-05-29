import scoverage.ScoverageKeys._

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.5"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
)
lazy val root = (project in file("."))
  .enablePlugins(CoverallsPlugin)
  .settings(
    name := "MinesweeperProjekt",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.14" % Test
    ),
    coverageEnabled := true,
    coverageFailOnMinimum := false,
    Test / fork := true
  )
