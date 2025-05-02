package de.htwg.controller

import de.htwg.utility.Observer
import de.htwg.model.Board
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers


class ControllerSpec extends AnyWordSpec with Matchers {

  class TestObserver extends Observer {
    var updated = false
    override def update: Unit = updated = true
  }
  "A Controller" should {

    "after creating a new game notify its observer" in {           // createNewBoard
      val board = new Board()
      val controller = new Controller(board)
      val observer = new TestObserver
      controller.add(observer)
      controller.createNewBoard(7, 10)    // Evtl Problem, falls falsches Board genommen wird.
      observer.updated should be(true)
      controller.board.size should be(7)
    }
    "after revealing a cell notify its observer" in {     // revealCell
      val board = new Board()
      val controller = new Controller(board)
      val observer = new TestObserver
      controller.add(observer)
      controller.revealCell(0, 0)
      observer.updated should be(true)
      controller.board.cells(0)(0).isRevealed should be(true)
    }
    "after flagging a cell notify its observer" in {      // flagCell
      val board = new Board()
      val controller = new Controller(board)
      val observer = new TestObserver
      controller.add(observer)
      controller.flagCell(0, 0)
      observer.updated should be (true)
      controller.board.cells(0)(0).isFlagged should be (true)
    }
    "after checking for the win notify its observer" in {     // checkWin
      val board = new Board(1, 1)
      val controller = new Controller(board)
      val observer = new TestObserver
      controller.add(observer)
      controller.board.cells(0)(0).isMine = true
      controller.board.cells(0)(0).isFlagged = true
      controller.checkWin() should be (true)
      observer.updated should be (true)
    }
    "after restting a game notify its observer" in {         // resetGame
      val board = new Board()
      val controller = new Controller(board)
      val observer = new TestObserver
      controller.add(observer)
      controller.board.cells(0)(0).isRevealed = true
      controller.board.cells(0)(1).isFlagged = true
      controller.resetGame()

      observer.updated should be (true)
      controller.board.cells(0)(0).isRevealed should be (false)
      controller.board.cells(0)(1).isFlagged should be (false)
      }
    }
}


