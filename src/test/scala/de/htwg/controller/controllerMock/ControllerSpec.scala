package de.htwg.controller.controllerMock

import de.htwg.controller.ControllerInterface
import de.htwg.controller.controllerMock.Controller
import de.htwg.factory.{BoardFactory, BoardFactoryInterface}
import de.htwg.model.BoardInterface
import de.htwg.utility.Observer
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class ControllerSpec extends AnyWordSpec {

  class DummyFactory extends BoardFactory {
    override def createBoard(): BoardInterface = new de.htwg.model.boardMock.Board()
  }

  class DummyObserver extends Observer {
    var updated = false

    override def update: String = {
      updated = true
      "updated"
    }
  }

  "A Mock Controller" should {

    val factory = new DummyFactory()
    val controller: ControllerInterface = new Controller(factory)

    "return the current board" in {
      controller.getBoard should not be null
    }

    "set a new board" in {
      val newBoard = new de.htwg.model.boardMock.Board()
      controller.setBoard(newBoard)
      controller.getBoard shouldBe newBoard
    }

    "create a new board using a factory" in {
      controller.createNewBoard(factory)
      controller.getBoard shouldBe a[BoardInterface]
    }

    "simulate revealing a cell (not at 0,0)" in {
      controller.revealCell(1, 1) shouldBe true
    }

    "simulate revealing a mine at (0,0)" in {
      controller.revealCell(0, 0) shouldBe false
    }

    "toggle flag state when flagging a cell" in {
      val initialFlagState = controller.asInstanceOf[Controller].flagged
      controller.flagCell(2, 2)
      controller.asInstanceOf[Controller].flagged shouldBe !initialFlagState
    }

    "return true for win condition if revealed" in {
      controller.revealCell(1, 1)
      controller.checkWin() shouldBe true
    }

    "return correct reset message" in {
      controller.resetGame() should include("Mock: Game reset")
      controller.asInstanceOf[Controller].resetCalled shouldBe true
    }

    "display the board normally or fully revealed" in {
      controller.displayBoardToString() should include("Mock Display")
      controller.displayBoardToString(true) should include("Mock Display All")
    }

    "return a new copy of the board" in {
      val copy = controller.copyBoard()
      copy shouldBe a[BoardInterface]
      copy should not be theSameInstanceAs(controller.getBoard)
    }

    "return correct remaining flags" in {
      controller.remainingFlags() shouldBe 1
    }

    "return mocked elapsed time" in {
      controller.getElapsedTime shouldBe 42
    }

    "have undo and redo stacks initially empty" in {
      controller.undoStackSize shouldBe 0
      controller.redoStackSize shouldBe 0
    }

    "allow adding and removing observers" in {
      val obs = new DummyObserver
      controller.add(obs)
      controller.remove(obs) // removal works silently
    }
  }
}
