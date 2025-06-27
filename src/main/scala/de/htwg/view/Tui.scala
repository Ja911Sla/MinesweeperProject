package de.htwg.view

//import de.htwg.command.*

import de.htwg.controller.ControllerInterface
import de.htwg.controller.controllerBase.{Controller, FlagCommand, SetCommand}
import de.htwg.utility.Observer
import de.htwg.controller.strategy.{CustomStrategy, GameModeStrategy}
import de.htwg.model.singleton.GameConfig
import de.htwg.view.state.{GameState, LostState, MenuState, PlayingState, WonState}

import scala.io.StdIn

class Tui (using var controller: ControllerInterface) extends Observer {
  var state: GameState = PlayingState
  private val flagPattern = "F ([A-Z])([1-9][0-9]*)".r
  private val revealPattern = "([A-Z])([1-9][0-9]*)".r

  def runObserverUpdate(): Unit = update


  def start(resetBoard: Boolean = true): String = {


    chooseDifficulty()
    if (resetBoard) controller.resetGame()
    if (controller.getBoard == null || controller.getBoard.cells.flatten.isEmpty) {
      chooseDifficulty()
      if (resetBoard) controller.resetGame()
    }
    println(
      """Willkommen zu Minesweeper!
        |Das Ziel ist es, alle Felder zu öffnen, ohne auf eine Mine zu treten.
        |Befehle:
        |  C3   -> Zelle aufdecken
        |  F C3 -> Flagge setzen/entfernen
        |  H    -> Hilfe anzeigen
        |  A    -> Anleitung anzeigen
        |  T    -> Zeit anzeigen
        |  M    -> Game Mode wechseln
        |  Q    -> Spiel beenden
        |""".stripMargin)

    shouldRun = true
    while (shouldRun) {
      if (state == PlayingState) {
        println(controller.displayBoardToString())
      }

      Option(scala.io.StdIn.readLine()) match {
        case Some(input) =>
          if (input.trim.toUpperCase == "Q") {
            println("Spiel beendet.")
            shouldRun = false
            guiQuitCallback() // tell GUI to quit
          } else {
            shouldRun = state.handleInput(input, this)
          }

        case None =>
          println("Eingabestrom beendet.")
          shouldRun = false
      }
    }
    "Game over."
  }


  def chooseDifficulty(): Unit = {
    if (controller.isDifficultySet) {
      println("Schwierigkeit bereits durch GUI gesetzt.")
      controller.add(this)
      println(controller.displayBoardToString()) // <- Damit TUI gleich was sieht
      return
    }
    // GUI hat noch keine Schwierigkeit gesetzt, also TUI übernimmt
    println("Wähle Schwierigkeitsgrad:")
    println("1 - Leicht (6x6, 5 Minen)")
    println("2 - Mittel (9x9, 15 Minen)")
    println("3 - Schwer (12x12, 35 Minen)")
    println("4 - Benutzerdefiniert")

    //println("SAVE - Spiel speichern")
    //println("LOAD - Spiel laden")



    val strategy: GameModeStrategy = StdIn.readLine("Eingabe: ") match {
      case "1" => GameConfig.getInstance.setCustom(6, 5)
        CustomStrategy
      case "2" => GameConfig.getInstance.setCustom(9, 15)
        CustomStrategy
      case "3" => GameConfig.getInstance.setCustom(12, 35)
        CustomStrategy
      case "4" =>
        val size = StdIn.readLine("Boardgröße: (2 bis 26) erlaubt) ").toInt
        val maxMines = size * size - 1
        val mines = StdIn.readLine(s"Anzahl Minen: (>= 1 und <= $maxMines erlaubt) ").toInt
        GameConfig.getInstance.setCustom(size, mines)
        CustomStrategy
      case _ =>
        println("Ungültige Eingabe. Standardmäßig 'Mittel' gewählt.")
        GameConfig.getInstance.setCustom(9, 15)
        CustomStrategy
    }

    controller.add(this)

    val factory = strategy.getBoardFactory()
    controller.createNewBoard(factory)
    controller.setDifficultySet(true)
  }


  def processInputLine(input: String): Boolean = {
    val i = input.trim.toUpperCase

    def parseCoordinate(input: String): Option[(Boolean, Int, Int)] = {
      i match {
        case flagPattern(rowChar, colStr) =>
          Some((true, rowChar.charAt(0) - 'A', colStr.toInt - 1))
        case revealPattern(rowChar, colStr) =>
          Some((false, rowChar.charAt(0) - 'A', colStr.toInt - 1))
        case _ => None
      }
    }

    i match {
      case "U" =>
        controller.undo()
        println(s"Undo verfügbar: ${controller.undoStackSize} | Redo verfügbar: ${controller.redoStackSize}")
        true

      case "R" =>
        controller.redo()
        println(s"Undo verfügbar: ${controller.undoStackSize} | Redo verfügbar: ${controller.redoStackSize}")
        true

      case "H" =>
        println(
          """
            |Willkommen zu Minesweeper!
            |Das Ziel ist es, alle Felder zu öffnen, ohne auf eine Mine zu treten.
            |Befehle:
            |  C3   -> Zelle aufdecken
            |  F C3 -> Flagge setzen/entfernen
            |  H    -> Hilfe anzeigen
            |  A    -> Anleitung anzeigen
            |  T    -> Zeit anzeigen
            |  M    -> Game Mode wechseln
          """.stripMargin)
        true

      case "A" =>
        println(
          """
            |Neu bei Minesweeper? Kein Problem!
            |Das Ziel ist es, Felder mit potentiellen Minen zu identifizieren und
            |mit einer Flagge zu markieren. Die Zahlen zeigen an, wie viele Minen angrenzen.
            |Nutze Logik – und etwas Glück!
          """.stripMargin)
        true

      case "SAVE" =>
        controller.save()
        println("Spiel gespeichert.")
        true

      case "LOAD" =>
        controller.load()
        println("Spielstand geladen.")
        println(controller.displayBoardToString())
        true

      case "T" =>
        println("Deine Spielzeit: " + controller.getElapsedTime + " Sekunden.")
        true

      case "M" =>
        state = MenuState
        state.handleInput(input, this)

      case _ =>
        parseCoordinate(i) match {
          case Some((isFlag, row, col)) =>
            if (isFlag) {
              val cmd = new FlagCommand(row, col, controller)
              controller.doAndStore(cmd)
              if (controller.checkWin()) {
                state = WonState
                state.handleInput(input, this)
                return false
              }
            } else {
              val cmd = new SetCommand(row, col, controller)
              controller.doAndStore(cmd)
              val cellOpt = Option(controller.getBoard.cells(row)(col))
              val safe = cellOpt.exists(c => c.isRevealed && !c.isMine)
              if (!safe) {
                state = LostState
                state.handleInput("", this)
                return true
              } else if (controller.checkWin()) {
                state = WonState
                state.handleInput(input, this); return false }
            }
            true

          case None =>
            println("Ungültige Eingabe. Nutze z.B. 'C3' oder 'F C3'.")
            true
        }
    }
  }

  override def update: String = {
    println(controller.displayBoardToString())
    ""
  }

  // gui added stuff
  private var shouldRun = true
  var guiQuitCallback: () => Unit = () => ()
  def requestQuit(): Unit = {
    shouldRun = false
  }
  def isRunning: Boolean = shouldRun
}
