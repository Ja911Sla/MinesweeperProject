package de.htwg.view

import de.htwg.controller.Controller
import de.htwg.factory.BoardFactory
import de.htwg.model.Board
import de.htwg.utility.Observer
import de.htwg.view.Tui
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.strategy.MediumStrategy

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}
import scala.Console

class TuiSpec extends AnyWordSpec {

  // Factory für Testzwecke 
  object TestBoardFactory extends BoardFactory {
    override def createBoard(): Board = new Board(9, 1)

    override def size: Int = 9

    override def mineCount: Int = 1
  }

  "The Tui" should {
    "quit the game with Q" in {
      val in = new ByteArrayInputStream("1\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      val controller = new Controller(TestBoardFactory)
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
      val in = new ByteArrayInputStream("1\nH\nQ\n".getBytes()) // Hilfe, dann beenden
      val out = new ByteArrayOutputStream()

      val controller = new Controller(TestBoardFactory) // Board() ist dein Model
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
      val in = new ByteArrayInputStream("1\nR\nQ\n".getBytes()) // Reset, dann quit
      val out = new ByteArrayOutputStream()

      val controller = new Controller(TestBoardFactory) // Board() ist dein Model
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Spiel zurückgesetzt.")
    }

    "toggle flag with F A1" in {
      //val in = new ByteArrayInputStream("F C3\nQ\n".getBytes())
      //val out = new ByteArrayOutputStream()

      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)
      tui.processInputLine("1")
      tui.processInputLine(" ")
      tui.processInputLine("F A1")

      controller.getBoard.cells(0)(0).isFlagged should be(true)
    }

    "reveal a cell with C3" in {


      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      tui.processInputLine("A1")

      controller.getBoard.cells(0)(0).isRevealed should be(true)
    }

    "handle invalid input" in {
      val in = new ByteArrayInputStream("1\nXYZ\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      val controller = new Controller(TestBoardFactory) // Board() ist dein Model
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
      val in = new ByteArrayInputStream("1\nA\nQ\n".getBytes()) // Anleitung, dann quit
      val out = new ByteArrayOutputStream()

      val controller = new Controller(TestBoardFactory) // Board() ist dein Model
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
    /*
    "lose the game when uncovering a mine" in {
      val in = new ByteArrayInputStream("1\n".getBytes()) // Wähle Leicht
      val out = new ByteArrayOutputStream()

      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      // Simuliere die Auswahl der Schwierigkeit (Leicht)
      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start(resetBoard = false)
        }
      }

      // Jetzt kannst du die Mine setzen, da der Controller fertig gesetzt ist
      controller.getBoard.reset()
      controller.getBoard.cells(0)(0).isMine = true

      // Nun Spiel mit dem eigentlichen Zug starten
      val gameplayIn = new ByteArrayInputStream("A1\nQ\n".getBytes())
      val gameplayOut = new ByteArrayOutputStream()

      Console.withIn(gameplayIn) {
        Console.withOut(new PrintStream(gameplayOut)) {
          val thread = new Thread(new Runnable {
            override def run(): Unit = {
              tui.start(resetBoard = false)
              ()
            }
          })
          thread.start()
          thread.join()
        }
      }

      val output = gameplayOut.toString
      println(output)
      output should include("BOOOM! Du hast verloren.")
    } */


    /*
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
  }*/
    /*"show winning message if all mines are flagged after flag input" in {


      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      // Manuelles Setup: nur eine Mine
      for (r <- 0 until controller.getBoard.size; c <- 0 until controller.getBoard.size) {
        controller.getBoard.cells(r)(c).isMine = false
        controller.getBoard.cells(r)(c).isFlagged = false
      }
      controller.getBoard.cells(0)(0).isMine = true

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        tui.processInputLine("\nF A1") // Flagge korrekt setzen = win
      }

      val output = out.toString
      output should include("Du hast gewonnen!")
    }
    "handle completely invalid command with processInputLine" in {
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        tui.processInputLine("invalid command")
      }

      out.toString should include("Ungültige Eingabe")
    }
  }
  */

    "show the elapsed time when T is entered" in {
      val in = new ByteArrayInputStream("1\nT\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Deine Spielzeit: 0 Sekunden.")
    }
    /*"show winning message after flagging all mines" in {
    //val board = Board(mineCount = 1)

    val controller = new Controller(TestBoardFactory)
    val tui = new Tui(controller)

    // Safe setup with 1 mine at A1
    controller.getBoard.cells(0)(0).isMine = true

    //val controller = new Controller(TestBoardFactory)
    //val tui = new Tui(controller)

    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      tui.processInputLine("1\nF A1")
    }

    val output = out.toString
    output should include("Du hast gewonnen!")
    output should include("Spielzeit: 0 Sekunden.")
  }*/
    /* "show losing message when stepping on a mine" in {


      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      controller.resetGame()
      controller.getBoard.cells(0)(0).isMine = true

      val in = new ByteArrayInputStream("1\nA1\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          val thread = new Thread(() => {
            tui.start(resetBoard = false);
            ()
          })
          thread.start()
          thread.join()
        }
      }

      val output = out.toString
      output should include("BOOOM! Du hast verloren.")
      output should include("Spielzeit: 0 Sekunden.")
    } */
    "win the game by revealing all safe cells" in {
      val board = Board(size = 2, mineCount = 1)

      // Leeres Setup
      for (r <- 0 until board.size; c <- 0 until board.size) {
        board.cells(r)(c).isMine = false
        board.cells(r)(c).isRevealed = false
        board.cells(r)(c).isFlagged = false
      }

      // Eine Mine auf A1 setzen
      board.cells(0)(0).isMine = true

      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        tui.processInputLine("A2") // (0,1)
        tui.processInputLine("B1") // (1,0)
        tui.processInputLine("B2") // (1,1) -> alle sicheren Felder aufgedeckt
      }

      val output = out.toString
      println(output) // Debug-Print
      output should include("Du hast gewonnen!")
      output should include("Spielzeit:")
    }
    "select Medium mode on input 2" in {
      val input = new ByteArrayInputStream("2\n".getBytes())
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(new PrintStream(output)) {
          val tui = new Tui(new Controller(MediumStrategy.getBoardFactory())) // Dummy Startwert
          val method = tui.getClass.getDeclaredMethod("chooseDifficulty")
          method.setAccessible(true)
          method.invoke(tui)

          tui.controller.getBoard.size should be(9)
          tui.controller.getBoard.mineCount should be(15)
        }
      }
    }
  }
}


