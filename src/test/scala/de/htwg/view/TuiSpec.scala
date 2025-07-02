package de.htwg.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.controller.ControllerInterface
import de.htwg.model.BoardInterface
import de.htwg.controller.controllerBase.{FlagCommand, SetCommand}
import de.htwg.view.state.{LostState, WonState}
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*
import org.scalatestplus.mockito.MockitoSugar

import java.io.ByteArrayInputStream

class TuiTest extends AnyWordSpec with Matchers with MockitoSugar {


  "A Tui" should {

    "recognize and process a reveal command like 'C3'" in {
      val controller = mock[ControllerInterface]
      val board = mock[BoardInterface]
      when(controller.getBoard).thenReturn(board)
      when(board.size).thenReturn(10)
      when(controller.isGameOver).thenReturn(false)
      when(controller.isWon).thenReturn(false)
      when(controller.remainingFlags()).thenReturn(5)
      when(controller.checkWin()).thenReturn(false)

      val tui = new Tui(using controller)
      val result = tui.processInputLine("C3")
      result shouldBe true
    }

    "recognize and process a flag command like 'F C3'" in {
      val controller = mock[ControllerInterface]
      val board = mock[BoardInterface]
      when(controller.getBoard).thenReturn(board)
      when(board.size).thenReturn(10)
      when(controller.isGameOver).thenReturn(false)
      when(controller.isWon).thenReturn(false)
      when(controller.remainingFlags()).thenReturn(5)
      when(controller.checkWin()).thenReturn(false)

      val tui = new Tui(using controller)
      val result = tui.processInputLine("F C3")
      result shouldBe true
    }

    "handle invalid input correctly" in {
      val controller = mock[ControllerInterface]
      val board = mock[BoardInterface]
      when(controller.getBoard).thenReturn(board)
      when(board.size).thenReturn(10)

      val tui = new Tui(using controller)
      val result = tui.processInputLine("invalid")
      result shouldBe true
    }

    "recognize and handle undo and redo commands" in {
      val controller = mock[ControllerInterface]
      val tui = new Tui(using controller)

      tui.processInputLine("U") shouldBe true
      verify(controller).undo()

      tui.processInputLine("R") shouldBe true
      verify(controller).redo()
    }

    "handle game over condition correctly" in {
      val controller = mock[ControllerInterface]
      val board = mock[BoardInterface]
      when(controller.getBoard).thenReturn(board)
      when(board.size).thenReturn(10)
      when(controller.isGameOver).thenReturn(true)
      when(controller.isWon).thenReturn(false)

      val tui = new Tui(using controller)
      val result = tui.processInputLine("C3")
      result shouldBe true
    }

    //    "test start() initialization logic" in {
    //      val controller = mock[ControllerInterface]
    //      val tui = new Tui(using controller)
    //      when(controller.isDifficultySet).thenReturn(true)
    //      when(controller.displayBoardToString()).thenReturn("Board")
    //
    //      val result = tui.start(resetBoard = false)
    //      result should include("Board")
    //    }

    //    "test chooseDifficulty() with default input" in {
    //      val controller = mock[ControllerInterface]
    //      when(controller.isDifficultySet).thenReturn(false)
    //
    //      val tui = new Tui(using controller)
    //
    //      // Simuliere Eingabe über StdIn (Workaround durch override)
    //      Console.withIn(new java.io.ByteArrayInputStream("1\n".getBytes())) {
    //        tui.chooseDifficulty()
    //      }
    //      verify(controller).createNewBoard(any())
    //      verify(controller).setDifficultySet(true)
    //    }

    "process valid input (reveal)" in {
      val controller = mock[ControllerInterface]
      val board = mock[BoardInterface]
      when(controller.getBoard).thenReturn(board)
      when(board.size).thenReturn(10)
      when(controller.isGameOver).thenReturn(false)
      when(controller.isWon).thenReturn(false)
      when(controller.remainingFlags()).thenReturn(5)
      when(controller.checkWin()).thenReturn(false)

      val tui = new Tui(using controller)
      tui.processInputLine("C3") shouldBe true
    }

    "process valid input (flag)" in {
      val controller = mock[ControllerInterface]
      val board = mock[BoardInterface]
      when(controller.getBoard).thenReturn(board)
      when(board.size).thenReturn(10)
      when(controller.isGameOver).thenReturn(false)
      when(controller.isWon).thenReturn(false)
      when(controller.remainingFlags()).thenReturn(5)
      when(controller.checkWin()).thenReturn(false)

      val tui = new Tui(using controller)
      tui.processInputLine("F C3") shouldBe true
    }

    //    "restartGame should reset and start new game" in {
    //      val controller = mock[ControllerInterface]
    //      val board = mock[BoardInterface]
    //      when(controller.getBoard).thenReturn(board)
    //      when(board.size).thenReturn(10)
    //      when(controller.remainingFlags()).thenReturn(5)
    //      when(controller.displayBoardToString()).thenReturn("ResetBoard")
    //
    //      val tui = new Tui(using controller)
    //      Console.withIn(new java.io.ByteArrayInputStream("1\n".getBytes())) {
    //        tui.restartGame()
    //      }
    //
    //      verify(controller, atLeastOnce()).resetGame()
    //      verify(controller).setDifficultySet(false)
    //      verify(controller).setDifficultySet(true)
    //    }

    "update should reflect game state changes" in {
      val controller = mock[ControllerInterface]
      val board = mock[BoardInterface]
      when(controller.remainingFlags()).thenReturn(3)
      when(controller.displayBoardToString()).thenReturn("SomeBoard")
      when(controller.isGameOver).thenReturn(false)
      when(controller.isWon).thenReturn(false)

      val tui = new Tui(using controller)
      val result = tui.update
      result shouldBe ""
    }

    "runObserverUpdate should call update" in {
      val controller = mock[ControllerInterface]
      val tui = new Tui(using controller)
      noException should be thrownBy tui.runObserverUpdate()
    }

    "requestQuit should change running state" in {
      val controller = mock[ControllerInterface]
      val tui = new Tui(using controller)
      tui.isRunning shouldBe true
      tui.requestQuit()
      tui.isRunning shouldBe false
    }

    //

    "start" should {
      "print board if already initialized and difficulty is set" in {
        val controller = mock[ControllerInterface]
        when(controller.isDifficultySet).thenReturn(true)
        when(controller.displayBoardToString()).thenReturn("MockBoard")

        val tui = new Tui(using controller)
        val result = tui.start(resetBoard = false)
        result shouldBe "Game over."

      }
    }

//    "chooseDifficulty" should {
//      "select medium if input is invalid" in {
//        val controller = mock[ControllerInterface]
//        when(controller.isDifficultySet).thenReturn(false)
//
//        val tui = new Tui(using controller)
//
//        // Simuliere ungültige Eingabe → default ist "Mittel"
//        Console.withIn(new java.io.ByteArrayInputStream("invalid\n".getBytes())) {
//          tui.chooseDifficulty()
//        }
//
//        verify(controller).createNewBoard(any())
//        verify(controller).setDifficultySet(true)
//      }
//    }




    "processInputLine with utility commands" should {
      "SAVE should trigger save on controller" in {
        val controller = mock[ControllerInterface]
        val tui = new Tui(using controller)

        tui.processInputLine("SAVE") shouldBe true
        verify(controller).save()
      }

      "LOAD should trigger load on controller" in {
        val controller = mock[ControllerInterface]
        when(controller.displayBoardToString()).thenReturn("Board")

        val tui = new Tui(using controller)
        tui.processInputLine("LOAD") shouldBe true

        verify(controller).load()
      }

      "T should print elapsed time" in {
        val controller = mock[ControllerInterface]
        when(controller.getElapsedTime).thenReturn(42)

        val tui = new Tui(using controller)
        tui.processInputLine("T") shouldBe true
      }
    }

    "input validation" should {
      "handle out-of-bounds coordinates safely" in {
        val controller = mock[ControllerInterface]
        val board = mock[BoardInterface]
        when(controller.getBoard).thenReturn(board)
        when(board.size).thenReturn(6)

        val tui = new Tui(using controller)
        val result = tui.processInputLine("Z9") // Z9 out of 6x6 board
        result shouldBe true
      }
    }

    "print tutorial text for 'A' command" in {
      val controller = mock[ControllerInterface]
      val tui = new Tui(using controller)

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        tui.processInputLine("A") shouldBe true
      }

      output.toString should include("Neu bei Minesweeper")
      output.toString should include("Die Zahlen zeigen an, wie viele Minen angrenzen")
    }

    "print help text for 'H' command" in {
      val controller = mock[ControllerInterface]
      val tui = new Tui(using controller)

      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        tui.processInputLine("H") shouldBe true
      }

      output.toString should include("Willkommen zu Minesweeper!")
      output.toString should include("F C3 -> Flagge setzen")
    }

    "skip if difficulty is already set" in {
      val controller = mock[ControllerInterface]
      when(controller.isDifficultySet).thenReturn(true)

      val tui = new Tui(using controller)
      val out = new java.io.ByteArrayOutputStream()

      Console.withOut(out) {
        tui.chooseDifficulty()
      }

      out.toString should include("Schwierigkeit bereits durch GUI gesetzt.")
    }

    "call chooseDifficulty and resetGame if difficulty is not set and resetBoard is true" in {
      val controller = mock[ControllerInterface]
      val board = mock[BoardInterface]

      when(controller.isDifficultySet).thenReturn(false)
      when(controller.displayBoardToString()).thenReturn("Board")
      when(controller.getBoard).thenReturn(board)
      when(board.size).thenReturn(6)

      val tui = spy(new Tui(using controller))

      // chooseDifficulty selbst stubben, da wir dessen Inhalt nicht testen wollen
      doNothing().when(tui).chooseDifficulty()

      Console.withIn(new ByteArrayInputStream("Q\n".getBytes())) {
        tui.start(resetBoard = true) shouldBe "Game over."
      }

      verify(tui).chooseDifficulty()
      verify(controller).resetGame()
    }

    "not call resetGame if resetBoard is false" in {
      val controller = mock[ControllerInterface]
      val board = mock[BoardInterface]

      when(controller.isDifficultySet).thenReturn(false)
      when(controller.displayBoardToString()).thenReturn("Board")
      when(controller.getBoard).thenReturn(board)
      when(board.size).thenReturn(6)

      val tui = spy(new Tui(using controller))
      doNothing().when(tui).chooseDifficulty()

      Console.withIn(new ByteArrayInputStream("Q\n".getBytes())) {
        tui.start(resetBoard = false) shouldBe "Game over."
      }

      verify(tui).chooseDifficulty()
      verify(controller, never()).resetGame()
    }

//    "call handleEndGame if state is WonState" in {
//      val controller = mock[ControllerInterface]
//      val board = mock[BoardInterface]
//
//      when(controller.isDifficultySet).thenReturn(true)
//      when(controller.displayBoardToString()).thenReturn("Board")
//      when(controller.getBoard).thenReturn(board)
//      when(board.size).thenReturn(6)
//
//      val tui = spy(new Tui(using controller))
//      tui.state = WonState
//
//      doNothing().when(tui).handleEndGame()
//
//      Console.withIn(new ByteArrayInputStream("Q\n".getBytes())) {
//        tui.start(resetBoard = false)
//      }
//
//      verify(tui).handleEndGame()
//    }
//
//    "call handleEndGame if state is LostState" in {
//      val controller = mock[ControllerInterface]
//      val board = mock[BoardInterface]
//
//      when(controller.isDifficultySet).thenReturn(true)
//      when(controller.displayBoardToString()).thenReturn("Board")
//      when(controller.getBoard).thenReturn(board)
//      when(board.size).thenReturn(6)
//
//      val tui = spy(new Tui(using controller))
//      tui.state = LostState
//
//      doNothing().when(tui).handleEndGame()
//
//      Console.withIn(new ByteArrayInputStream("Q\n".getBytes())) {
//        tui.start(resetBoard = false)
//      }
//
//      verify(tui).handleEndGame()
//    }
  }
}

