package de.htwg.controller

import de.htwg.utility.Observer
import de.htwg.model.Board
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers


class ControllerSpec extends AnyWordSpec with Matchers {

  "A Controller" should {

    val board = new Board(size = 3, mineCount = 0) // kleines, leeres Board für Test
    val controller = new Controller(board)

    "reveal a cell" in {
      controller.revealCell(0, 0)
      board.cells(0)(0).isRevealed shouldBe true
    }
  }
}


