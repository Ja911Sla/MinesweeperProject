package de.htwg

import de.htwg.model.Board
import de.htwg.view.Tui
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import de.htwg.controller.Controller
import de.htwg.utility.Observer
import scala.Console
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class TuiSpec extends AnyWordSpec {

  "The Tui" should {
    "quit the game with Q" in {
      val in = new ByteArrayInputStream("Q\n".getBytes())
      val out = new ByteArrayOutputStream()

      val controller = new Controller(Board())
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Willkommen zu Minesweeper!")
      output should include("Spiel beendet.")
    }

    "show help when H is entered" in {
      val in = new ByteArrayInputStream("H\nQ\n".getBytes()) // Hilfe, dann beenden
      val out = new ByteArrayOutputStream()

      val controller = new Controller(Board()) // Board() ist dein Model
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Befehle:")
      output should include("F C3 -> Flagge setzen")
    }

    "reset the game with R" in {
      val in = new ByteArrayInputStream("R\nQ\n".getBytes()) // Reset, dann quit
      val out = new ByteArrayOutputStream()

      val controller = new Controller(Board()) // Board() ist dein Model
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Spiel zurückgesetzt.")
    }

    "toggle flag with F C3" in {
      val in = new ByteArrayInputStream("F C3\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      val controller = new Controller(Board())
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Willkommen zu Minesweeper!")
    }

    "reveal a cell with C3" in {
      val in = new ByteArrayInputStream("C3\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      val controller = new Controller(Board())
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Willkommen zu Minesweeper!")
    }

    "handle invalid input" in {
      val in = new ByteArrayInputStream("XYZ\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      val controller = new Controller(Board()) // Board() ist dein Model
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Ungültige Eingabe")
    }
    // new code
    "show instructions when A is entered" in {
      val in = new ByteArrayInputStream("A\nQ\n".getBytes()) // Anleitung, dann quit
      val out = new ByteArrayOutputStream()

      val controller = new Controller(Board()) // Board() ist dein Model
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Neu bei Minesweeper? Kein Problem!")
      output should include("Ziel ist es Felder mit potentiellen Minen zu identifizieren")
    }
    // new code
    //more new code
    "lose the game when uncovering a mine" in {
      val controller = new Controller(Board())
      val tui = new Tui(controller)

      // Stelle sicher, dass Board initial korrekt gesetzt ist
      controller.board.reset()
      controller.board.cells(0)(0).isMine = true // Setze Mine manuell

      val in = new ByteArrayInputStream("A1\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          val thread = new Thread(() => tui.start(resetBoard = false)) // <- wichtig!
          thread.start()
          thread.join()
        }
      }

      val output = out.toString
      println(output)
      output should include("BOOOM! Du hast verloren.")
    }


    "win the game when all mines are flagged" in {
      val board = Board(mineCount = 1)

      // Initialisiere Board manuell
      for (r <- 0 until board.size; c <- 0 until board.size) {
        board.cells(r)(c).isMine = false
        board.cells(r)(c).isFlagged = false
      }
      board.cells(0)(0).isMine = true // Setze eine einzelne Mine

      val controller = new Controller(board)
      val tui = new Tui(controller)

      val in = new ByteArrayInputStream("F A1\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start(resetBoard = false) // <- verhindert das Zurücksetzen des Boards
        }
      }

      val output = out.toString
      println(output)
      output should include("Du hast gewonnen!")
    }
  }
}