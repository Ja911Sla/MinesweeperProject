package de.htwg.view

import de.htwg.controller.Controller
import de.htwg.utility.Observer
import scala.io.StdIn

class Tui(val controller: Controller) extends Observer {
    controller.add(this)

    def start(resetBoard: Boolean = true): Unit = {
        if (resetBoard) controller.resetGame()
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
      """.stripMargin
        )

        var running = true
        while (running) {
            controller.displayBoard()
            val input = StdIn.readLine()
            if (input.trim.toUpperCase == "Q") {
                running = false
                println("Spiel beendet.")
            } else {
                running = processInputLine(input)
            }
        }
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

            case move if move.matches("F [A-I][1-9]") =>
                val row = move.charAt(2) - 'A'
                val col = move.charAt(3) - '1'
                controller.flagCell(row, col)
                if (controller.checkWin()) {
                    controller.displayBoard(true)
                    println("Du hast gewonnen!")
                }
                true

            case move if move.matches("[A-I][1-9]") =>
                val row = move.charAt(0) - 'A'
                val col = move.charAt(1) - '1'
                val safe = controller.revealCell(row, col)
                if (!safe) {
                    controller.displayBoard(true)
                    println("BOOOM! Du hast verloren.")
                    return  false
                } else if (controller.checkWin()) {
                    controller.displayBoard(true)
                    println("Du hast gewonnen!")
                    return false
                }
                true
            case _ =>
                println("Ungültige Eingabe. Nutze z.B. 'C3' oder 'F C3'.")
                true
        }
    }

    override def update: Unit = {
        println("Board wurde aktualisiert.")
    }
}
