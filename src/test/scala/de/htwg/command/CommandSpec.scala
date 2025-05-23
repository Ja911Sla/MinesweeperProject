import de.htwg.command.SetCommand
import de.htwg.factory.BoardFactory
import de.htwg.model.Board
import de.htwg.controller.Controller
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*


class CommandSpec extends AnyWordSpec {

  object TestBoardFactory extends BoardFactory {
    override def createBoard(): Board = new Board(2, 1) 
  }
  
  "A SetCommand" should {
    "reveal a cell when doStep is called" in {
      val controller = new Controller(TestBoardFactory)
      val row = 0
      val col = 0

      val cmd = new SetCommand(row, col, controller)

      controller.getBoard.cells(row)(col).isRevealed shouldBe false

      cmd.doStep()

      controller.getBoard.cells(row)(col).isRevealed shouldBe true
    }

    "undo the reveal when undoStep is called" in {
      val controller = new Controller(TestBoardFactory)
      val row = 0
      val col = 0

      val cmd = new SetCommand(row, col, controller)

      cmd.doStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe true

      cmd.undoStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe false
    }

    "redo the reveal when redoStep is called" in {
      val controller = new Controller(TestBoardFactory)
      val row = 0
      val col = 0

      val cmd = new SetCommand(row, col, controller)

      cmd.doStep()
      cmd.undoStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe false

      cmd.redoStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe true
    }

    "work with Controller.doAndStore and undo" in {
      val controller = new Controller(TestBoardFactory)
      val row = 1
      val col = 1

      val cmd = new SetCommand(row, col, controller)
      controller.doAndStore(cmd)

      controller.getBoard.cells(row)(col).isRevealed shouldBe true

      controller.undo()
      controller.getBoard.cells(row)(col).isRevealed shouldBe false
    }
  }
}
