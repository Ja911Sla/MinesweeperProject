package de.htwg
import scala.io.StdIn // eingabe von 

case class Tui(){
    val board = new Board()

    def start(): Unit = {
        board.reset()
        println("""
        |Willkommen zu Minesweeper!
        |Das Ziel ist es, alle Felder zu öffnen, ohne auf eine Mine zu treten.
        |Befehle:
        |  C3   -> Zelle aufdecken
        |  F C3 -> Flagge setzen/entfernen
        |  Q    -> Spiel beenden
        |  R    -> Spiel zurücksetzen
        |  H    -> Hilfe anzeigen
        |  A    -> Anleitung anzeigen
        |""".stripMargin) // stripMargin entfernt die Einrückung und gibt den Text in einem lesbaren Format aus

        var running = true
        while (running) {
            board.display()
            val input = StdIn.readLine().trim.toUpperCase // konvertiert alle eingaben in großbuchstaben und entfernt Leerzeichen
            input match {
                case i if i == "Q" => // Q für quit
                running = false 
                println("Spiel beendet.")

                case i if i == "R" => // R für reset
                board.reset()
                println("Spiel zurückgesetzt.")

                case i if i == "H" => // H für Hilfe
                println("""
                         |Willkommen zu Minesweeper!
                         |Das Ziel ist es, alle Felder zu öffnen, ohne auf eine Mine zu treten.
                         |Befehle:
                         |  C3   -> Zelle aufdecken
                         |  F C3 -> Flagge setzen/entfernen
                         |  Q    -> Spiel beenden
                         |  R    -> Spiel zurücksetzen
                         |  H    -> Hilfe anzeigen
                         |  A    -> Anleitung anzeigen
                         |""".stripMargin) 

                 // vielliecht könntest du hier ein A für Anleitung machen, was erklärt wie man das Spiel spielt.

                case i if i == "A" =>   // A für Anleitung
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
                          |""".stripMargin)


                case i if i.matches("F [A-I][1-9]") => // regex für Flagge setzen/entfernen
                val row = i.charAt(2) - 'A' 
                val col = i.charAt(3) - '1' 
                board.toggleFlag(row, col)  

                case i if i.matches("[A-I][1-9]") => // regex für Zelle aufdecken
                val row = i.charAt(0) - 'A'
                val col = i.charAt(1) - '1'
                val safe = board.reveal(row, col) 

                if (!safe) {
                    board.display(true)
                    println("BOOOM! Du hast verloren.")
                    running = false
                } else if (board.checkWin()) {
                    board.display(true)
                    println("Du hast gewonnen!")
                    running = false
                }

                case _ => // alle anderen Eingaben sind ungültig
                println("Ungültige Eingabe. Nutze z.B. 'C3' oder 'F C3'.")
            }
        }
    }
}

