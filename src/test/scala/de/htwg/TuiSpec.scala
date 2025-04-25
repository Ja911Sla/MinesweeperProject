package de.htwg

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
    }
}
