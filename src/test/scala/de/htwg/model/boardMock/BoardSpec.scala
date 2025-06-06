package de.htwg.model.boardMock


import de.htwg.model.boardBase.GameCell
import de.htwg.model.BoardInterface
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class BoardSpec extends AnyWordSpec {

  "A BoardMock" should {

    val board: BoardInterface = new Board()

    "have correct size and mineCountt" in {
      board.size shouldBe 9
      board.mineCount shouldBe 10
    }

    "reset properly" in {
      board.reset() shouldBe "Mock: Board reset."
    }

    "place mines and return fixed value" in {
      board.placeMines() shouldBe 1
    }

    "always reveal successfully" in {
      board.reveal(0, 0) shouldBe true
    }

    "check inBounds correctly" in {
      board.inBounds(0, 0) shouldBe true
      board.inBounds(-1, 0) shouldBe false
      board.inBounds(0, -1) shouldBe false
      board.inBounds(9, 0) shouldBe false
      board.inBounds(0, 9) shouldBe false
    }

    "return 0 for adjacent mines" in {
      board.countAdjacentMines(0, 0) shouldBe 0
    }

    "toggle flags correctly" in {
      board.cells(1)(1).isFlagged shouldBe false
      board.toggleFlag(1, 1)
      board.cells(1)(1).isFlagged shouldBe true
      board.toggleFlag(1, 1)
      board.cells(1)(1).isFlagged shouldBe false
    }

    "report win as always true" in {
      board.checkWin() shouldBe true
    }

    "display correctly" in {
      board.display(revealAll = true) shouldBe "Mock: Board Display"
      board.display(revealAll = false) shouldBe "Mock: Board Display"
    }

    "return a bomb count string" in {
      board.bombCountDisplayString() shouldBe "Mock: Bomb Count"
    }

    "copy itself correctly" in {
      val copiedBoard = board.copyBoard()
      copiedBoard should not be theSameInstanceAs(board)
      copiedBoard.size shouldBe board.size
      copiedBoard.mineCount shouldBe board.mineCount
    }

    "return 0 for remaining flags" in {
      board.remainingFlags() shouldBe 0
    }
  }
}

