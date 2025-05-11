package de.htwg.view

import de.htwg.controller.Controller
import de.htwg.utility.Observer
import scala.io.StdIn

class Tui(val controller: Controller) extends Observer {
    controller.add(this)

    def start(resetBoard: Boolean = true): String = {
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

            case move if move.matches("F [A-I][1-9]") =>
                val row = move.charAt(2) - 'A'
                val col = move.charAt(3) - '1'
                controller.flagCell(row, col)
                if (controller.checkWin()) {
                    controller.displayBoardToString(true)
                    println("Du hast gewonnen!")
                    println("Spielzeit: "+ controller.getElapsedTime + " Sekunden.")
                    return false
                }
                true

            case move if move.matches("[A-I][1-9]") =>
                val row = move.charAt(0) - 'A'
                val col = move.charAt(1) - '1'
                val safe = controller.revealCell(row, col)
                if (!safe) {
                    println(controller.displayBoardToString(true))
                    println("BOOOM! Du hast verloren.")
                    println("Spielzeit: "+ controller.getElapsedTime + " Sekunden.")
                    return false  // Beendet die Schleife oben korrekt
                } else if (controller.checkWin()) {
                    println(controller.displayBoardToString(true))
                    println("Du hast gewonnen!")
                    println("Spielzeit: "+ controller.getElapsedTime + " Sekunden.")
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
