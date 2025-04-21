package de.htwg
import scala.util.Random

case class Board(val size: Int = 9, val mineCount: Int = 10) {
    val cells: Array[Array[GameCell]] = Array.fill(size, size)(GameCell()) //cells füllt das 2D Array mit GameCell Objekten
    val directions = List((-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)) // liste von alle möglichen Nachbarn

    def reset(): Unit = { //Jan
        // Nestart des Spiels, das heißt heißt jede Zelle im 2D Array wird mit neuen GameCell Objekten gefüllt
        // danach muss man die Minen neu platzieren

    }

    def placeMines(): Unit = { //Jan
        // platziere die Minen zufällig (Random) im 2D Array, mineCount gibt an wie viele Minen es maximal gibt
        // wenn die Zelle noch keine Mine hat (isMine), dann platziere eine Mine. 
        
    }

    def reveal(row: Int, col: Int): Boolean = {
        if (cells(row)(col).isFlagged || cells(row)(col).isRevealed) return true // Zelle bereits aufgedeckt oder markiert
        if (cells(row)(col).isMine) return false // Mine aufgedeckt, Spiel verloren
        val count = countAdjacentMines(row, col)  // Zähle die Minen in den Nachbarzellen
        cells(row)(col).mineCount = count // Setze die Anzahl der benachbarten Minen in der Zelle
        cells(row)(col).isRevealed = true // Zelle aufdecken
        if (count == 0) revealAdjacent(row, col) // Wenn keine Minen in den Nachbarzellen, decke angrenzende Zellen auf
        true
    }

    def inBounds(r: Int, c: Int): Boolean = { // nimmt die Zeile r und Spalte c als Parameter im 2D Array
        r >= 0 && r < size && c >= 0 && c < size // Überprüfe, ob die Zelle innerhalb der Grenzen des Spielfelds liegt
    }

    def countAdjacentMines(row: Int, col: Int): Int = { // zählt die Anzahl der Minen in den Nachbarzellen
        directions.count { case (dr, dc) => // .count Iteriert über alle Nachbarn die die isMine Bedingung erfüllt. dr= direchtion row, dc= direction column 
        val nr = row + dr // addiert man die Zeilenverschiebung zu der aktuellen Zeile hat man die Koordinate der Nachbarzelle
        val nc = col + dc // genau das gleiche macht man hier für die Spalte
        inBounds(nr, nc) && cells(nr)(nc).isMine // Prüft die Grenzen und ob die Nachbarzelle eine Mine hat
        }
       
    }

    def revealAdjacent(row: Int, col: Int): Unit = { //Jan
        // Iteriere über alle Nachbarn indem du die positionen berechneste wie oben nr = row + dr 
        // Überprüfe ob Nachbarzelle im Spielfeldgrenze liegt (inBounds) und noch nicht markiert ist (isFlagged)
        // erst nach beiden Bedingungen wird die Zelle aufgedeckt (reveal)
        
    }

     def toggleFlag(row: Int, col: Int): Unit = { // Jan
        // Wenn die Zelle nicht aufgedeckt ist, dann toggle die Flagge (isFlagged) der Zelle
        // Wenn die Zelle aufgedeckt ist, dann mache nichts
        // toggle bedeutet, dass wenn die Flagge gesetzt ist, sie entfernt wird und umgekehrt
    }

    def checkWin(): Boolean = { 
        val flaggedMines = cells.flatten.count(c => c.isFlagged && c.isMine) // Zähle die Minen die markiert sind
        val totalFlags = cells.flatten.count(_.isFlagged) // Zähle die Gesamtzahl der gesetzten Flaggen
        flaggedMines == mineCount && totalFlags == mineCount  // Gewonnen wenn Anzahl der markierten Minen und Flaggen gleich der Gesamtzahl der Minen ist
    }



    def display(revealAll: Boolean = false): Unit = {
        println("  1 2 3 4 5 6 7 8 9") // Spaltenüberschrift
        for (r <- 0 until size) { // Iteriere über die Zeilen
            print((r + 'A').toChar + " ") // Konvertiere die Zeilenindizes in Buchstaben (A-I)
            for (c <- 0 until size) {  // Iteriere über die Spalten
                val cell = cells(r)(c) 
                if (revealAll && cell.isMine) print("M ") // mine M wird gezeigt wenn alles aufgedeckt werden soll 
                else if (cell.isFlagged) print("F ") // zeige flag F wenn es gesetzte wird
                else if (cell.isRevealed) print(if (cell.mineCount == 0) "# " else s"${cell.mineCount} ")  // zeige leere Zelle # wenn alle Nachbarzellen keine Mine haben
                else print("_ ")
            }
            println()
        }
    }
}
