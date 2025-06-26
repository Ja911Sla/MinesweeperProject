package de.htwg.controller.controllerBase

import de.htwg.controller.factory.BoardFactory
import de.htwg.model.boardBase.Board
import de.htwg.utility.Observer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
// import de.htwg.command.*
import de.htwg.controller.*
import de.htwg.controller.controllerBase.{Command, Controller, SetCommand}

import java.io.{ByteArrayOutputStream, PrintStream}

class ControllerSpec extends AnyWordSpec with Matchers {

  // Factory für Testzwecke 
  object TestBoardFactory extends BoardFactory {
    override def createBoard(): Board = new Board(2, 1)
  }

  class TestObserver extends Observer {
    var updated = false

    override def update: String = {
      updated = true
      "updated"
    }
  }

  class MockCommand extends Command {
    var doCalled = false
    var redoCalled = false
    var undoCalled = false

    override def doStep(): Unit = {

    }
    override def redoStep(): Unit = {
      redoCalled = true
    }

    override def undoStep(): Unit = {
      undoCalled = true
    }
  }


  "A Controller" should {

    "after creating a new game notify its observer" in {
      val controller = new Controller(TestBoardFactory)
      val observer = new TestObserver
      controller.add(observer)

      controller.createNewBoard(TestBoardFactory) 

      observer.updated should be(true)
      controller.getBoard.size should be(2)
    }

    "after revealing a cell notify its observer" in {
      val controller = new Controller(TestBoardFactory)
      val observer = new TestObserver
      controller.add(observer)

      controller.revealCell(0, 0)

      observer.updated should be(true)
      controller.getBoard.cells(0)(0).isRevealed should be(true)
    }

    "after flagging a cell notify its observer" in {
      val controller = new Controller(TestBoardFactory)
      val observer = new TestObserver
      controller.add(observer)

      controller.flagCell(0, 0)

      observer.updated should be(true)
      controller.getBoard.cells(0)(0).isFlagged should be(true)
    }

    "after checking for the win notify its observer" in {
      //val controller = new Controller(() => new Board(1, 1)) // anonyme Factory
      //val observer = new TestObserver

      val controller = new Controller(TestBoardFactory)
      val observer = new TestObserver

      controller.add(observer)

      controller.getBoard.cells(0)(0).isMine = true
      controller.getBoard.cells(0)(0).isFlagged = true
      controller.getBoard.cells(1)(0).isMine = false
      controller.getBoard.cells(1)(0).isFlagged = false
      controller.getBoard.cells(1)(0).isRevealed = true

      controller.getBoard.cells(1)(1).isMine = false
      controller.getBoard.cells(1)(1).isFlagged = false
      controller.getBoard.cells(1)(1).isRevealed = true

      controller.getBoard.cells(0)(1).isMine = false
      controller.getBoard.cells(0)(1).isFlagged = false
      controller.getBoard.cells(0)(1).isRevealed = true


      controller.checkWin() should be(true)
      observer.updated should be(true)
    }

    "after resetting a game notify its observer" in {
      val controller = new Controller(TestBoardFactory)
      val observer = new TestObserver
      controller.add(observer)

      controller.getBoard.cells(0)(0).isRevealed = true
      controller.getBoard.cells(0)(1).isFlagged = true

      controller.resetGame()

      observer.updated should be(true)
      controller.getBoard.cells(0)(0).isRevealed should be(false)
      controller.getBoard.cells(0)(1).isFlagged should be(false)
    }

    "create a copy" in {
      val controller = new Controller(TestBoardFactory)
      val observer = new TestObserver
      controller.add(observer)

      controller.getBoard.cells(0)(0).isRevealed = false
      controller.getBoard.cells(0)(0).isMine = true
      controller.getBoard.cells(1)(0).isRevealed = true
      controller.getBoard.cells(1)(0).isMine = false
      controller.getBoard.cells(1)(1).isRevealed = false
      controller.getBoard.cells(1)(1).isFlagged = true
      controller.getBoard.cells(0)(1).isRevealed = false
      controller.getBoard.cells(0)(1).isMine = false

      val output = controller.copyBoard()

      controller.getBoard.cells(0)(0).isRevealed should be(false)
      controller.getBoard.cells(0)(0).isMine should be(true)
      controller.getBoard.cells(1)(0).isRevealed should be(true)
      controller.getBoard.cells(1)(0).isMine should be(false)
      controller.getBoard.cells(1)(1).isRevealed should be(false)
      controller.getBoard.cells(1)(1).isFlagged should be(true)
      controller.getBoard.cells(0)(0).isRevealed should be(false)
      controller.getBoard.cells(0)(1).isMine should be(false)
    }

    "to put a board in place and notify its observer" in {
      val controller = new Controller(TestBoardFactory)
      val observer = new TestObserver
      controller.add(observer)

      controller.getBoard.cells(0)(0).isRevealed = false
      controller.getBoard.cells(0)(0).isMine = true
      controller.getBoard.cells(1)(0).isRevealed = true
      controller.getBoard.cells(1)(0).isMine = false
      controller.getBoard.cells(1)(1).isRevealed = false
      controller.getBoard.cells(1)(1).isFlagged = true
      controller.getBoard.cells(0)(1).isRevealed = false
      controller.getBoard.cells(0)(1).isMine = false

      val controllerSafe = controller.copyBoard()

      controller.resetGame()

      controller.setBoard(controllerSafe)

      observer.updated should be(true)

      controller.getBoard.cells(0)(0).isRevealed should be(false)
      controller.getBoard.cells(0)(0).isMine should be(true)
      controller.getBoard.cells(1)(0).isRevealed should be(true)
      controller.getBoard.cells(1)(0).isMine should be(false)
      controller.getBoard.cells(1)(1).isRevealed should be(false)
      controller.getBoard.cells(1)(1).isFlagged should be(true)
      controller.getBoard.cells(0)(0).isRevealed should be(false)
      controller.getBoard.cells(0)(1).isMine should be(false)

    }

    "push something on stack" in {
      val controller = new Controller(TestBoardFactory)
      val observer = new TestObserver
      controller.add(observer)

    }

    "reveal a cell via SetCommand and undo it" in {
      val controller = new Controller(TestBoardFactory)
      val row = 0
      val col = 0


      controller.getBoard.cells(row)(col).isRevealed shouldBe false
      val cmd = new SetCommand(row, col, controller)

      cmd.doStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe true


      cmd.undoStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe false

      cmd.redoStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe true
    }

    "push SetCommand on stack and undo via controller" in {
      val controller = new Controller(TestBoardFactory)
      val row = 1
      val col = 1

      val cmd = new SetCommand(row, col, controller)
      controller.doAndStore(cmd)

      controller.getBoard.cells(row)(col).isRevealed shouldBe true

      controller.undo()

      controller.getBoard.cells(row)(col).isRevealed shouldBe false
    }

    "print message when undo is called with empty command stack" in {
      val controller = new Controller(TestBoardFactory)

      val outContent = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outContent)) {
        controller.undo()
      }

      outContent.toString.trim shouldBe "Bereits am Anfang des Spiels – nichts mehr rückgängig zu machen."
    }


      "print message when redo is called with empty stack" in {
        val controller = new Controller(TestBoardFactory)

        val outContent = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(outContent)) {
          controller.redo()
        }

        outContent.toString.trim shouldBe "Du bist bereits im aktuellen Spielstand – nichts zum Wiederholen."
      }

      "execute redo and update stacks" in {
        val controller = new Controller(TestBoardFactory)
        val cmd = new MockCommand()

        // simulate a redoable command
        controller.getRedoStack.push(cmd)

        controller.redo()

        // redoStep should have been called
        cmd.redoCalled shouldBe true

        // undoStack should now contain the command
        controller.getUndoStack should contain(cmd)

        // redoStack should be empty
        controller.getRedoStack shouldBe empty
      }

      "return correct undoStackSize and redoStackSize" in {
        val controller = new Controller(TestBoardFactory)

        controller.getUndoStack shouldBe empty
        controller.getRedoStack shouldBe empty
        controller.undoStackSize shouldBe 0
        controller.redoStackSize shouldBe 0

        controller.getUndoStack.push(new MockCommand())
        controller.getRedoStack.push(new MockCommand())

        controller.undoStackSize shouldBe 1
        controller.redoStackSize shouldBe 1
      }
    }
}
