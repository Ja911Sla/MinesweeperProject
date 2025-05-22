package de.htwg.view

import de.htwg.controller.Controller
import de.htwg.strategy._
import de.htwg.utility.Observer
import de.htwg.factory._
import de.htwg.singleton._
import de.htwg.state._

import scala.io.StdIn

class Tui(var controller: Controller) extends Observer {
  var state: GameState = PlayingState
  private val flagPattern = "F ([A-Z])([1-9][0-9]*)".r
  private val revealPattern = "([A-Z])([1-9][0-9]*)".r

  def start(resetBoard: Boolean = true): String = {
    chooseDifficulty()
    if (resetBoard) controller.resetGame()
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
        |""".stripMargin)

    var running = true
    while (running) {
      println(controller.displayBoardToString())
      val input = StdIn.readLine()
      running = state.handleInput(input, this)
    }
    "Game over."
  }

   def chooseDifficulty(): Unit = {
    println("Wähle Schwierigkeitsgrad:")
    println("1 - Leicht (6x6, 5 Minen)")
    println("2 - Mittel (9x9, 15 Minen)")
    println("3 - Schwer (12x12, 35 Minen)")
    println("4 - Benutzerdefiniert")

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

    val factory = strategy.getBoardFactory()
    controller = new Controller(factory)
    controller.add(this)
  }

    def processInputLine(input: String): Boolean = {
        val i = input.trim.toUpperCase

        i match {

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
        """.stripMargin
                )
                true

            case "A" =>
                println(
                    """
                      |Neu bei Minesweeper? Kein Problem!
                      |Das Ziel ist es Felder mit potentiellen Minen zu identifizieren und
                      |mit einer Flagge zu markieren. Es gibt eine sichtbare Menge an Minen mit
                      |dem der Spieler immer weiß wie viele Minen es noch zu erkennen gibt.
                      |
                      |Jedes sichere Feld was revealed wurde, gibt eine Zahl aus um zu signalisieren wieviele Bomben
                      |sich in direkter Nähe um das Feld befinden. Nun muss man durch Logik, Kombinatorik und manchmal
                      |auch durch etwas Glück sich auf die Suche nach den Minen begeben.
                      |
                      |Viel Erfolg!
                        """.stripMargin
                )
                true

            case "T" =>
                println("Deine Spielzeit: " + controller.getElapsedTime + " Sekunden.")
                true

            case "M" =>
              state = MenuState
              state.handleInput(input, this)

            case flagPattern(rowChar, colStr) =>
              val row = rowChar.charAt(0) - 'A'
              val col = colStr.toInt - 1
              controller.flagCell(row, col)
              if (controller.checkWin()) {
                state = WonState
                state.handleInput(input, this)
                return false
              }
              true

            case revealPattern(rowChar, colStr) =>
              val row = rowChar.charAt(0) - 'A'
              val col = colStr.toInt - 1
              val safe = controller.revealCell(row, col)
              if (!safe) {
                state = LostState
                state.handleInput(input, this)
                return true // muss true sein damit sich das spiel jetzt nicht mehr sofort beendet
              } else if (controller.checkWin()) {
                state = WonState
                state.handleInput(input, this)
                return false
              }
                true
            case _ =>
                println("Ungültige Eingabe. Nutze z.B. 'C3' oder 'F C3'.")
                true
        }
    }

    override def update:String = {
       "Board wurde aktualisiert."
    }
}
