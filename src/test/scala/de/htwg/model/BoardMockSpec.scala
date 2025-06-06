package de.htwg.model

import de.htwg.model.BoardInterface
import de.htwg.model.boardMock.Board
import de.htwg.model.boardBase.GameCell
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class BoardMockSpec extends AnyWordSpec {

  "A BoardMock" should {

    val board: BoardInterface = new Board()

    "have the correct size and mine count" in {
      board.size shouldBe 9
      board.mineCount shouldBe 10
    }

    "return a 2D array of GameCells" in {
      board.cells.length shouldBe board.size
      board.cells(0).length shouldBe board.size
      board.cells(0)(0).isInstanceOf[GameCell] shouldBe true
    }

    "successfully reveal a cell" in {
      board.reveal(1, 1) shouldBe true
    }

    "correctly report win condition" in {
      board.checkWin() shouldBe true
    }

    "return a new copy of the board" in {
      val copy = board.copyBoard()
      copy should not be theSameInstanceAs(board)
      copy.size shouldBe board.size
    }

    "always count 0 adjacent mines in mock" in {
      board.countAdjacentMines(0, 0) shouldBe 0
    }

    "correctly report in-bounds values" in {
      board.inBounds(0, 0) shouldBe true
      board.inBounds(8, 8) shouldBe true
      board.inBounds(-1, 0) shouldBe false
      board.inBounds(0, -1) shouldBe false
      board.inBounds(9, 9) shouldBe false
    }

    "allow revealAdjacent to be called without exception" in {
      noException should be thrownBy board.revealAdjacent(4, 4)
    }

    "always place 1 mine (mocked)" in {
      board.placeMines() shouldBe 1
    }

    "toggle a flag on a cell" in {
      val cell = board.cells(0)(0)
      val before = cell.isFlagged
      board.toggleFlag(0, 0)
      cell.isFlagged shouldBe !before
    }

    "display the board as a string" in {
      board.display() should include("Mock: Board Display")
    }

    "return the bomb count display string" in {
      board.bombCountDisplayString() should include("Mock: Bomb Count")
    }

    "report remaining flags as 0" in {
      board.remainingFlags() shouldBe 0
    }

    "return a reset string" in {
      board.reset() should include("Mock: Board reset.")
    }
  }
}

