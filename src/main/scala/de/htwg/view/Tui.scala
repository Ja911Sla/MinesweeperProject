package de.htwg.view

import de.htwg.controller.Controller
import de.htwg.strategy._
import de.htwg.utility.Observer
import de.htwg.factory._
import de.htwg.singleton._

import scala.io.StdIn

class Tui(var controller: Controller) extends Observer {
  val flagPattern = "F ([A-Z])([1-9][0-9]*)".r
  val revealPattern = "([A-Z])([1-9][0-9]*)".r
    
    def start(resetBoard: Boolean = true): String = {
        chooseDifficulty()
        if (resetBoard) controller.resetGame()
        println(
            """Willkommen zu Minesweeper!
              |Das Ziel ist es, alle Felder zu öffnen, ohne auf eine Mine zu treten.
              |Befehle:
              |  C3   -> Zelle aufdecken
              |  F C3 -> Flagge setzen/entfernen
              |  Q    -> Spiel beenden
              |  R    -> Spiel zurücksetzen
              |  H    -> Hilfe anzeigen
              |  A    -> Anleitung anzeigen
              |  T    -> Zeit anzeigen
              |""".stripMargin)

        var running = true
        while (running) {
            println(controller.displayBoardToString())
            val input = StdIn.readLine()
            if (input.trim.toUpperCase == "Q") {
                println("Spiel beendet.")
                running = false
            } else {
                val continue = processInputLine(input) // <- nutze das Rückgabeergebnis
                running = continue                     // <- setzt running entsprechend
            }
        }
        "Game over."
    }

  private def chooseDifficulty(): Unit = {
    println("Wähle Schwierigkeitsgrad:")
    println("1 - Leicht (6x6, 5 Minen)")
    println("2 - Mittel (9x9, 15 Minen)")
    println("3 - Schwer (12x12, 35 Minen)")
    println("4 - Benutzerdefiniert")

    val strategy: GameModeStrategy = StdIn.readLine("Eingabe: ") match {
      case "1" => EasyStrategy
      case "2" => MediumStrategy
      case "3" => HardStrategy
      case "4" =>
        val size = StdIn.readLine("Boardgröße: (2 bis 26) erlaubt) ").toInt
        val maxMines = size * size - 1
        val mines = StdIn.readLine(s"Anzahl Minen: (>= 1 und <= $maxMines erlaubt) ").toInt
        GameConfig.setCustom(size, mines)
        CustomStrategy
      case _ =>
        println("Ungültige Eingabe. Standardmäßig 'Mittel' gewählt.")
        MediumStrategy
    }

    val factory = strategy.getBoardFactory()
    controller = new Controller(factory)
    controller.add(this)
  }
  
    def processInputLine(input: String): Boolean = {
        val i = input.trim.toUpperCase

        i match {
            case "R" =>
                controller.resetGame()
                println("Spiel zurückgesetzt.")
                true

            case "H" =>
                println(
                    """
                      |Willkommen zu Minesweeper!
                      |Das Ziel ist es, alle Felder zu öffnen, ohne auf eine Mine zu treten.
                      |Befehle:
                      |  C3   -> Zelle aufdecken
                      |  F C3 -> Flagge setzen/entfernen
                      |  Q    -> Spiel beenden
                      |  R    -> Spiel zurücksetzen
                      |  H    -> Hilfe anzeigen
                      |  A    -> Anleitung anzeigen
                      |  T    -> Zeit anzeigen
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
              println(
                """
                  |Modus-Menü:
                  |1 - Schwierigkeit ändern (Spiel wird neugestartet)
                  |2 - Zurück zum Spiel
                  |""".stripMargin
              )
              val choice = scala.io.StdIn.readLine().trim
              choice match {
                case "1" =>
                  chooseDifficulty() // reuse your existing difficulty selector
                  controller.resetGame() // reset with new difficulty
                  println("Spiel mit neuer Schwierigkeit gestartet.")
                  true
                case "2" =>
                  println("Zurück zum Spiel.")
                  true
                case _ =>
                  println("Ungültige Eingabe. Zurück zum Spiel.")
                  true
              }

            case flagPattern(rowChar, colStr) =>
              val row = rowChar.charAt(0) - 'A'
              val col = colStr.toInt - 1
              controller.flagCell(row, col)
              if (controller.checkWin()) {
                println(controller.displayBoardToString(true))
                println("Du hast gewonnen!")
                println("Spielzeit: " + controller.getElapsedTime + " Sekunden.")
                return false
              }
              true

            case revealPattern(rowChar, colStr) =>
              val row = rowChar.charAt(0) - 'A'
              val col = colStr.toInt - 1
              val safe = controller.revealCell(row, col)
              if (!safe) {
                println(controller.displayBoardToString(true))
                println("BOOOM! Du hast verloren.")
                println("Spielzeit: " + controller.getElapsedTime + " Sekunden.")
                return false
              } else if (controller.checkWin()) {
                println(controller.displayBoardToString(true))
                println("Du hast gewonnen!")
                println("Spielzeit: " + controller.getElapsedTime + " Sekunden.")
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
