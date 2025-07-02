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
  private var isInitialized: Boolean = false // Add this variable

  def runObserverUpdate(): Unit = update

  def start(resetBoard: Boolean = true): String = {
    if (isInitialized) {
      println("TUI bereits initialisiert.")
      return controller.displayBoardToString()
    }

    isInitialized = true
    controller.add(this) // Observer nur einmal hinzufügen

    // GUI initialisieren, wenn noch nicht geschehen
    if (Gui.tui == null) {
      Gui.initialize(controller)
      Gui.top.visible = true // GUI sichtbar machen
      Gui.tui = this
      Gui.tuiThread = new Thread(new Runnable {
        override def run(): Unit = {
          // TUI läuft bereits, keine erneute Initialisierung nötig
        }
      })
      Gui.tuiThread.setDaemon(true)
      Gui.tuiThread.start()
    }

    if (!controller.isDifficultySet) {
      chooseDifficulty()
      if (resetBoard) controller.resetGame()
    } else {
      println("Schwierigkeit bereits durch GUI gesetzt.")
      println(controller.displayBoardToString())
    }

    shouldRun = true
    while (shouldRun) {
      if (state == PlayingState) {
        println(controller.displayBoardToString())
      } else if (state == WonState || state == LostState) {
        handleEndGame()
      }
      //Option(scala.io.StdIn.readLine()) match {
      Option(StdIn.readLine()) match {
        case Some(input) =>
          if (input.trim.toUpperCase == "Q") {
            println("Spiel beendet.")
            shouldRun = false
            guiQuitCallback()
            //sys.exit(0)
          } else {
            shouldRun = state.handleInput(input, this)
          }
        case None =>
          println("Eingabestrom beendet.")
          shouldRun = false
          //sys.exit(0)
      }
    }
    "Game over."
  }

  def chooseDifficulty(): Unit = {
    if (controller.isDifficultySet) {
      println("Schwierigkeit bereits durch GUI gesetzt.")
      return
    }

    println("Wähle Schwierigkeitsgrad:")
    println("1 - Leicht (6x6, 5 Minen)")
    println("2 - Mittel (9x9, 15 Minen)")
    println("3 - Schwer (12x12, 35 Minen)")
    println("4 - Benutzerdefiniert")

    println("Eingabe: ")
    val strategy: GameModeStrategy = StdIn.readLine() match {
      case "1" => GameConfig.getInstance.setCustom(6, 5); CustomStrategy
      case "2" => GameConfig.getInstance.setCustom(9, 15); CustomStrategy
      case "3" => GameConfig.getInstance.setCustom(12, 35); CustomStrategy
      case "4" =>
        println("Boardgröße: (2 bis 26 erlaubt)")
        val size = StdIn.readLine().toInt

        if (size < 2 || size > 26) {
          println("Ungültige Boardgröße. Standardmäßig 'Mittel' gewählt.")
          GameConfig.getInstance.setCustom(9, 15)
          CustomStrategy
        } else {
          val maxMines = size * size - 1
          println(s"Anzahl Minen (>= 1 und <= $maxMines erlaubt):")
          val mines = StdIn.readLine().toInt

          if (mines < 1 || mines > maxMines) {
            println("Ungültige Minenanzahl. Standardmäßig 'Mittel' gewählt.")
            GameConfig.getInstance.setCustom(9, 15)
            CustomStrategy
          } else {
            GameConfig.getInstance.setCustom(size, mines)
            CustomStrategy
          }
        }
      case _ =>
        println("Ungültige Eingabe. Standardmäßig 'Mittel' gewählt.")
        GameConfig.getInstance.setCustom(9, 15)
        CustomStrategy
    }

    val factory = strategy.getBoardFactory()
    controller.createNewBoard(factory)
    controller.setDifficultySet(true)
    if (Gui != null && !java.awt.GraphicsEnvironment.isHeadless()) Gui.startGame(factory)
    // GUI aktualisieren
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

      case "N" =>
        restartGame()
        true

      case _ =>
        parseCoordinate(i) match {
          case Some((isFlag, row, col)) =>
            //neu start
            if (row < 0 || row >= controller.getBoard.size || col < 0 || col >= controller.getBoard.size) {
              println("Ungültige Koordinaten!")
              return true
            }
            if (controller.isGameOver || controller.isWon) {
              handleEndGame()
              return true
            } //neu end
            if (isFlag) {
              val cmd = new FlagCommand(row, col, controller)
              controller.doAndStore(cmd)
              if (controller.checkWin()) {
                state = WonState
                handleEndGame()
                return false
              }
            } else {
              val cmd = new SetCommand(row, col, controller)
              controller.doAndStore(cmd)
              //neu start
              if (controller.isGameOver) {
                state = LostState
                handleEndGame()
                return true //neu end

              } else if (controller.checkWin()) {
                state = WonState
                //neu start
                handleEndGame()
                return true //neu end
                /*
                state.handleInput(input, this); return false */
              }
            }
            //neu start
            println(s"Verbleibende Flaggen: ${controller.remainingFlags()}") // neu end
            true

          case None =>
            println("Ungültige Eingabe. Nutze z.B. 'C3' oder 'F C3'.")
            true
        }
    }
  }

  private def handleEndGame(): Unit = {
    if (state == WonState) {
      println("Gewonnen, Glückwunsch!")
    } else if (state == LostState) {
      println("💥 Game Over – Du hast eine Mine erwischt!")
    }
    println("Tippe 'N' für ein neues Spiel oder 'Q' zum Beenden.")
  }
  def restartGame(): Unit = {
    controller.setDifficultySet(false)
    controller.resetGame()
    state = MenuState
    chooseDifficulty()
    controller.resetGame()
    state = PlayingState
    println(s"Verbleibende Flaggen: ${controller.remainingFlags()}")
    println(controller.displayBoardToString())
    if (Gui != null && !java.awt.GraphicsEnvironment.isHeadless()) Gui.restartGame()
    // GUI synchronisieren
  }

  override def update: String = {
    if (controller.isGameOver) state = LostState
    else if (controller.isWon) state = WonState
    println(s"Verbleibende Flaggen: ${controller.remainingFlags()}")
    println(controller.displayBoardToString())
    if (controller.isGameOver || controller.isWon) handleEndGame()
    ""
  }

  // gui added stuff
  private var shouldRun = true
  var guiQuitCallback: () => Unit = () => {
    println("TUI hat Quit-Signal gegeben. Beende GUI...")
    if (Gui != null && Gui.top != null) Gui.top.close()

  }

  def requestQuit(): Unit = {
    shouldRun = false
  }
  def isRunning: Boolean = shouldRun
}
