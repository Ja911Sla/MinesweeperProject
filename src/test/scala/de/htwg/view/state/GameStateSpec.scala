package de.htwg.view.state

import de.htwg.controller.controllerBase.{Controller, SetCommand}
import de.htwg.controller.factory.BoardFactory
import de.htwg.model.boardBase.Board
import de.htwg.view.Tui
import de.htwg.view.state.{IdleState, LostState, MenuState, PlayingState, QuitState, RestartState, WonState}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.controller.controllerBase.given_ControllerInterface
import de.htwg.controller.ControllerInterface
import de.htwg.fileio.FileIOInterface
import org.mockito.Mockito.verify
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.*
import org.scalatest.matchers.should.Matchers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}
import scala.Console



class GameStateSpec extends AnyWordSpec with Matchers with MockitoSugar {



  class DummyFileIO extends FileIOInterface {
    override def save(board: de.htwg.model.BoardInterface): Unit = {}

    override def load(): de.htwg.model.BoardInterface = new Board(2, 1)
  }

  object TestBoardFactory extends BoardFactory {
    override def createBoard(): Board = new Board(9, 1)
  }

//  "IdleState" should {
//    "start a new game and switch to PlayingState" in {
//      val in = new ByteArrayInputStream("1\n".getBytes()) // Select easy mode
//      val out = new ByteArrayOutputStream()
//
//      // für die Tui erstellung erzeige ich einen controller und binde es mit given
//      val controller = new Controller(TestBoardFactory, new DummyFileIO())
//      given ControllerInterface = controller
//      val tui = new Tui() {
//        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
//      }
//
//
//
//      Console.withIn(in) {
//        Console.withOut(new PrintStream(out)) {
//          IdleState.handleInput("any", tui)
//        }
//      }
//
//      tui.state shouldBe PlayingState
//      out.toString should include("Starting new game")
//    }
//  }

  "PlayingState" should {

    "process input that ends the game by hitting a mine" in {
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      given ControllerInterface = controller
      val tui = new Tui() {
        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
      }



      controller.getBoard.cells(0)(0).isMine = true

      tui.state = PlayingState
      val result = tui.state.handleInput("A1", tui)
      result shouldBe true

      val quit = tui.state.handleInput("Q", tui)
      quit should be (false)
    }
  }

  "WonState" should {
    "display winning message" in {
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      given ControllerInterface = controller
      val tui = new Tui() {
        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
      }




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
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      given ControllerInterface = controller
      val tui = new Tui() {
        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
      }



      Console.withOut(new PrintStream(out)) {
        LostState.handleInput("any", tui)
      }

      out.toString should include("BOOOM! Du hast verloren.")
      out.toString should include("Spielzeit")
    }
    "perform undo when 'U' is input" in {
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      given ControllerInterface = controller
      val tui = new Tui() {
        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
      }

      tui.state = LostState

      // fake einen Spielzug, damit etwas zum Undo da ist
      val row = 0
      val col = 0
      val cmd = new SetCommand(row, col, tui.controller)
      tui.controller.doAndStore(cmd)
      tui.controller.getBoard.cells(row)(col).isRevealed shouldBe true

      Console.withOut(new PrintStream(out)) {
        LostState.handleInput("U", tui)
      }

      tui.state shouldBe PlayingState
      tui.controller.getBoard.cells(row)(col).isRevealed shouldBe false
    }
    "restart game when 'R' is input in LostState" in {
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory, new DummyFileIO())

      given ControllerInterface = controller

      val tui = new Tui() {
        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
      }

      tui.state = LostState

      Console.withOut(new PrintStream(out)) {
        LostState.handleInput("R", tui)
      }

      tui.state shouldBe PlayingState
      out.toString should include("Spiel zurückgesetzt")
    }
  }

  "QuitState" should {
    "print quit message and return false" in {
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory, new DummyFileIO())

      given ControllerInterface = controller

      val tui = new Tui() {
        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
      }


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
      val controller = new Controller(TestBoardFactory, new DummyFileIO())

      given ControllerInterface = controller

      val tui = new Tui() {
        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
      }


      Console.withOut(new PrintStream(out)) {
        RestartState.handleInput("any", tui)
      }

      tui.state shouldBe PlayingState
      out.toString should include("Spiel zurückgesetzt")
    }
  }

  "MenuState" should {
//    "restart game when selecting option 1" in {
//      val in = new ByteArrayInputStream("1\n1\n".getBytes()) // Select "1" to change difficulty, then select easy mode again
//      val out = new ByteArrayOutputStream()
//      val controller = new Controller(TestBoardFactory, new DummyFileIO())
//
//      given ControllerInterface = controller
//
//      val tui = new Tui() {
//        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
//      }
//
//
//      Console.withIn(in) {
//        Console.withOut(new PrintStream(out)) {
//          MenuState.handleInput("any", tui)
//        }
//      }
//
//      tui.state shouldBe PlayingState
//      out.toString should include("Spiel mit neuer Schwierigkeit gestartet")
//    }

    "go back to game when selecting option 2" in {
      val in = new ByteArrayInputStream("2\n".getBytes())
      val out = new ByteArrayOutputStream()
      val controller = new Controller(TestBoardFactory, new DummyFileIO())

      given ControllerInterface = controller

      val tui = new Tui() {
        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
      }


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
      val controller = new Controller(TestBoardFactory, new DummyFileIO())

      given ControllerInterface = controller

      val tui = new Tui() {
        override def start(resetBoard: Boolean): String = "" // <-- richtige Signatur
      }


      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          MenuState.handleInput("any", tui)
        }
      }

      tui.state shouldBe PlayingState
      out.toString should include("Ungültige Eingabe. Zurück zum Spiel")
    }

    "handle input '1' by restarting game with new difficulty" in {
      val controller = mock[ControllerInterface]
      val tui = spy(new Tui(using controller))
      doNothing().when(tui).chooseDifficulty()

      val output = new java.io.ByteArrayOutputStream()

      Console.withOut(output) {
        Console.withIn(new ByteArrayInputStream("1\n".getBytes())) {
          MenuState.handleInput("1", tui) shouldBe true
        }
      }

      verify(tui).chooseDifficulty()
      verify(controller).resetGame()
      tui.state shouldBe PlayingState
      output.toString should include("Spiel mit neuer Schwierigkeit gestartet.")
    }
    
  }
}
