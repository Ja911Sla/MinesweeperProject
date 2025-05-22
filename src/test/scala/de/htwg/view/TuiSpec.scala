package de.htwg.view

import de.htwg.controller.Controller
import de.htwg.factory.BoardFactory
import de.htwg.model.Board
import de.htwg.singleton.GameConfig
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.strategy.*

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}
import scala.Console
import de.htwg.state.{IdleState, LostState, MenuState, PlayingState, QuitState, RestartState, WonState}

class TuiSpec extends AnyWordSpec {

  object TestBoardFactory extends BoardFactory {
    override def createBoard(): Board = new Board(9, 1)
    
  }

  "The Tui" should {
    "handle Q quit command" in {
      val tui = new Tui(new Controller(TestBoardFactory))
      tui.state = PlayingState
      val running = tui.state.handleInput("Q", tui)
      running should be(false)
      tui.state should be(QuitState)
    }

    "show help when H is entered" in {
      val in = new ByteArrayInputStream("1\nH\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Befehle:")
    }

    "show instructions when A is entered" in {
      val in = new ByteArrayInputStream("1\nA\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Neu bei Minesweeper? Kein Problem!")
    }

    "reset the game with R" in {
      val in = new ByteArrayInputStream("1\nR\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Spiel zurückgesetzt.")
    }

    "show elapsed time when T is entered" in {
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

    "toggle flag with F A1" in {
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)
      tui.processInputLine("F A1")
      controller.getBoard.cells(0)(0).isFlagged should be(true)
    }

    "reveal a cell with A1" in {
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)
      tui.processInputLine("A1")
      controller.getBoard.cells(0)(0).isRevealed should be(true)
    }

    "handle invalid input" in {
      val in = new ByteArrayInputStream("1\nXYZ\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Ungültige Eingabe")
    }

    "win the game by revealing all safe cells" in {
      val board = Board(size = 2, mineCount = 1)
      for (r <- 0 until board.size; c <- 0 until board.size) {
        board.cells(r)(c).isMine = false
      }
      board.cells(0)(0).isMine = true

      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        tui.processInputLine("A2") // (0,1)
        tui.processInputLine("B1") // (1,0)
        tui.processInputLine("B2") // (1,1) -> all safe cells revealed
      }

      val output = out.toString
      output should include("Du hast gewonnen!")
    }

    "select Medium mode on input 2" in {
      val in = new ByteArrayInputStream("2\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          val tui = new Tui(new Controller(BoardFactory.getInstance))
          val method = tui.getClass.getDeclaredMethod("chooseDifficulty")
          method.setAccessible(true)
          method.invoke(tui)
          tui.controller.getBoard.size should be(9)
          tui.controller.getBoard.mineCount should be(15)
        }
      }
    }
    "skip board reset when resetBoard is false" in {
      val in = new ByteArrayInputStream("1\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      // Manually flag a cell BEFORE starting
      controller.flagCell(0, 0)
      controller.getBoard.cells(0)(0).isFlagged should be(true)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start(resetBoard = false)
        }
      }

      // After start, the cell should still be flagged if reset did NOT run
      controller.getBoard.cells(0)(0).isFlagged should be(true)
    }
    "select Hard mode on input 3" in {
      val in = new ByteArrayInputStream("3\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          val tui = new Tui(new Controller(BoardFactory.getInstance))
          val method = tui.getClass.getDeclaredMethod("chooseDifficulty")
          method.setAccessible(true)
          method.invoke(tui)
          tui.controller.getBoard.size should be(12)
          tui.controller.getBoard.mineCount should be(35)
        }
      }
    }
    "select Custom mode on input 4 with size and mines" in {
      val in = new ByteArrayInputStream("4\n5\n3\n".getBytes()) // Size 5, Mines 3
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          val tui = new Tui(new Controller(BoardFactory.getInstance))
          val method = tui.getClass.getDeclaredMethod("chooseDifficulty")
          method.setAccessible(true)
          method.invoke(tui)
          tui.controller.getBoard.size should be(5)
          tui.controller.getBoard.mineCount should be(3)
        }
      }
    }
    "handle invalid difficulty selection with fallback to Medium" in {
      val in = new ByteArrayInputStream("invalid\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          val tui = new Tui(new Controller(BoardFactory.getInstance))
          val method = tui.getClass.getDeclaredMethod("chooseDifficulty")
          method.setAccessible(true)
          method.invoke(tui)

          val output = out.toString
          output should include("Ungültige Eingabe. Standardmäßig 'Mittel' gewählt.")
          tui.controller.getBoard.size should be(9)
          tui.controller.getBoard.mineCount should be(15)
        }
      }
    }
    "show winning message when all mines are flagged after flag input" in {
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      // Set up a single mine at A1
      controller.getBoard.cells(0)(0).isMine = true

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        tui.processInputLine("F A1") // Flagging A1 should trigger win
      }

      val output = out.toString
      output should include("Du hast gewonnen!")
      output should include("Spielzeit: ")
    }
    "show losing message when revealing a mine" in {
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      // Set up a single mine at A1
      controller.getBoard.cells(0)(0).isMine = true

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        tui.processInputLine("A1") // Revealing A1 should trigger loss
      }

      val output = out.toString
      output should include("BOOOM! Du hast verloren.")
      output should include("Spielzeit: ")
    }

    "handle M mode change correctly and go back to game" in {
      val tui = new Tui(new Controller(TestBoardFactory))

      val in = new ByteArrayInputStream("2\n".getBytes())
      Console.withIn(in) {
        tui.state = MenuState
        tui.state.handleInput("dummy", tui) // The input here is ignored in MenuState
      }

      tui.state should be(PlayingState)
    }
    "handle invalid input in M menu and return to game" in {
      val in = new ByteArrayInputStream("1\nM\ninvalid\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          tui.start()
        }
      }

      val output = out.toString
      output should include("Ungültige Eingabe. Zurück zum Spiel.")
    }


  }
}
