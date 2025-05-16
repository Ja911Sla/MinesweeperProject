package de.htwg.controller

import de.htwg.factory.BoardFactory
import de.htwg.utility.Observer
import de.htwg.model.Board
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ControllerSpec extends AnyWordSpec with Matchers {

  // Factory für Testzwecke 
  object TestBoardFactory extends BoardFactory {
    override def createBoard(): Board = new Board(2, 1)

    override def size: Int = 9

    override def mineCount: Int = 1
  }

  class TestObserver extends Observer {
    var updated = false

    override def update: String = {
      updated = true
      "updated"
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
  }
}
