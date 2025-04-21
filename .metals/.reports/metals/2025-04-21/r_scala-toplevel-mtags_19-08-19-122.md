error id: file:///C:/HTWG_Module/Minesweeper_Project/MinesweeperProject/src/main/scala/de/htwg/Tui.scala:[16..17) in Input.VirtualFile("file:///C:/HTWG_Module/Minesweeper_Project/MinesweeperProject/src/main/scala/de/htwg/Tui.scala", "package de.htwg._
import scala.io.StdIn // eingabe von der Konsole

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
      |""".stripMargin)

    var running = true
    while (running) {
      board.display()
      val input = StdIn.readLine().trim.toUpperCase
      input match {
        case i if i == "Q" =>
          running = false
          println("Spiel beendet.")

        case i if i == "R" =>
          board.reset()
          println("Spiel zurückgesetzt.")

        case i if i == "H" =>
          println("Hilfe: Nutze C3 um ein Feld aufzudecken oder F C3 um ein Flag zu setzen.")

        case i if i.matches("F [A-I][1-9]") =>
          val row = i.charAt(2) - 'A'
          val col = i.charAt(3) - '1'
          board.toggleFlag(row, col)

        case i if i.matches("[A-I][1-9]") =>
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

        case _ =>
          println("Ungültige Eingabe. Nutze z.B. 'C3' oder 'F C3'.")
      }
    }
  }
}

")
file:///C:/HTWG_Module/Minesweeper_Project/MinesweeperProject/src/main/scala/de/htwg/Tui.scala:1: error: expected identifier; obtained uscore
package de.htwg._
                ^
#### Short summary: 

expected identifier; obtained uscore