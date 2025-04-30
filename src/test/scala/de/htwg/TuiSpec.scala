package de.htwg

import de.htwg.model.Board
import de.htwg.view.Tui
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class TuiSpec extends AnyWordSpec {

  "The Tui" should {
    "quit the game with Q" in {
      val in = new ByteArrayInputStream("Q\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          Tui().start()
        }
      }

      val output = out.toString
      output should include("Willkommen zu Minesweeper!")
      output should include("Spiel beendet.")
    }

    "show help when H is entered" in {
      val in = new ByteArrayInputStream("H\nQ\n".getBytes()) // Hilfe, dann beenden
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          Tui().start()
        }
      }

      val output = out.toString
      output should include("Befehle:")
      output should include("F C3 -> Flagge setzen")
    }

    "reset the game with R" in {
      val in = new ByteArrayInputStream("R\nQ\n".getBytes()) // Reset, dann quit
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          Tui().start()
        }
      }

      val output = out.toString
      output should include("Spiel zurückgesetzt.")
    }

    "toggle flag with F C3" in {
      val in = new ByteArrayInputStream("F C3\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          Tui().start()
        }
      }

      val output = out.toString
      output should include("Willkommen zu Minesweeper!")
    }

    "reveal a cell with C3" in {
      val in = new ByteArrayInputStream("C3\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          Tui().start()
        }
      }

      val output = out.toString
      output should include("Willkommen zu Minesweeper!")
    }

    "handle invalid input" in {
      val in = new ByteArrayInputStream("XYZ\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          Tui().start()
        }
      }

      val output = out.toString
      output should include("Ungültige Eingabe")
    }
    // new code
    "show instructions when A is entered" in {
      val in = new ByteArrayInputStream("A\nQ\n".getBytes()) // Anleitung, dann quit
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          Tui().start()
        }
      }

      val output = out.toString
      output should include("Neu bei Minesweeper? Kein Problem!")
      output should include("Ziel ist es Felder mit potentiellen Minen zu identifizieren")
    }
    // new code
    //more new code
    "lose the game when uncovering a mine" in {
      val tui = Tui()

      val in = new ByteArrayInputStream("A1\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          val thread = new Thread(() => tui.start())
          thread.start()

          // Warte kurz, bis `reset()` durchgelaufen ist
          Thread.sleep(300)

          // Jetzt Mine setzen
          tui.board.cells(0)(0).isMine = true

          thread.join()
        }
      }

      val output = out.toString
      println(output) // <- falls du debuggen willst
      output should include("BOOOM! Du hast verloren.")
    }
    "win the game when all mines are flagged" in {
      val customBoard = Board(mineCount = 1)

      // Alles vorbereiten BEVOR das Spiel startet
      for (r <- 0 until customBoard.size; c <- 0 until customBoard.size) {
        customBoard.cells(r)(c).isMine = false
        customBoard.cells(r)(c).isFlagged = false
      }
      customBoard.cells(0)(0).isMine = true // A1 ist Mine

      val tui = view.Tui(board = customBoard)

      val in = new ByteArrayInputStream("F A1\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          //tui.start(resetBoard = false)
         
        }
      }

      val output = out.toString
      println(output)
      output should include("Du hast gewonnen!")
    }
  }
}
