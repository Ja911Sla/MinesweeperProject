package de.htwg.state

import de.htwg.controller.Controller
import de.htwg.factory.BoardFactory
import de.htwg.model.Board
import de.htwg.view.Tui
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}
import scala.Console

class GameStateSpec extends AnyWordSpec {

  object TestBoardFactory extends BoardFactory {
    override def createBoard(): Board = new Board(9, 1)
  }

  "IdleState" should {
    "start a new game and switch to PlayingState" in {
      val in = new ByteArrayInputStream("1\n".getBytes()) // Select easy mode
      val out = new ByteArrayOutputStream()
      val tui = new Tui(new Controller(TestBoardFactory))

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          IdleState.handleInput("any", tui)
        }
      }

      tui.state shouldBe PlayingState
      out.toString should include("Starting new game")
    }
  }

  "PlayingState" should {

    "process input that ends the game by hitting a mine" in {
      val controller = new Controller(TestBoardFactory)
      val tui = new Tui(controller)

      controller.getBoard.cells(0)(0).isMine = true

      tui.state = PlayingState
      val result = tui.state.handleInput("A1", tui)
      result shouldBe false
    }
  }

  "WonState" should {
    "display winning message" in {
      val out = new ByteArrayOutputStream()
      val tui = new Tui(new Controller(TestBoardFactory))

      Console.withOut(new PrintStream(out)) {
        WonState.handleInput("any", tui)
      }

      out.toString should include("Du hast gewonnen!")
      out.toString should include("Spielzeit")
    }
  }

  "LostState" should {
    "display losing message" in {
      val out = new ByteArrayOutputStream()
      val tui = new Tui(new Controller(TestBoardFactory))

      Console.withOut(new PrintStream(out)) {
        LostState.handleInput("any", tui)
      }

      out.toString should include("BOOOM! Du hast verloren.")
      out.toString should include("Spielzeit")
    }
  }

  "QuitState" should {
    "print quit message and return false" in {
      val out = new ByteArrayOutputStream()
      val tui = new Tui(new Controller(TestBoardFactory))

      val result = Console.withOut(new PrintStream(out)) {
        QuitState.handleInput("any", tui)
      }

      result shouldBe false
      out.toString should include("Danke für's Spielen")
    }
  }

  "RestartState" should {
    "reset game and return to PlayingState" in {
      val out = new ByteArrayOutputStream()
      val tui = new Tui(new Controller(TestBoardFactory))

      Console.withOut(new PrintStream(out)) {
        RestartState.handleInput("any", tui)
      }

      tui.state shouldBe PlayingState
      out.toString should include("Spiel zurückgesetzt")
    }
  }

  "MenuState" should {
    "restart game when selecting option 1" in {
      val in = new ByteArrayInputStream("1\n1\n".getBytes()) // Select "1" to change difficulty, then select easy mode again
      val out = new ByteArrayOutputStream()
      val tui = new Tui(new Controller(TestBoardFactory))

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          MenuState.handleInput("any", tui)
        }
      }

      tui.state shouldBe PlayingState
      out.toString should include("Spiel mit neuer Schwierigkeit gestartet")
    }

    "go back to game when selecting option 2" in {
      val in = new ByteArrayInputStream("2\n".getBytes())
      val out = new ByteArrayOutputStream()
      val tui = new Tui(new Controller(TestBoardFactory))

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          MenuState.handleInput("any", tui)
        }
      }

      tui.state shouldBe PlayingState
      out.toString should include("Zurück zum Spiel")
    }

    "handle invalid option and go back to game" in {
      val in = new ByteArrayInputStream("invalid\n".getBytes())
      val out = new ByteArrayOutputStream()
      val tui = new Tui(new Controller(TestBoardFactory))

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          MenuState.handleInput("any", tui)
        }
      }

      tui.state shouldBe PlayingState
      out.toString should include("Ungültige Eingabe. Zurück zum Spiel")
    }
  }
}
