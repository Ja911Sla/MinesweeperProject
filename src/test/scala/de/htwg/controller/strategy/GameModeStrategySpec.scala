package de.htwg.controller.strategy

import de.htwg.controller.controllerBase.Controller
import de.htwg.controller.factory.BoardFactory
import de.htwg.fileio.FileIOInterface
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import scala.Console
import de.htwg.model.boardBase.Board
import de.htwg.model.singleton.GameConfig

class GameModeStrategySpec extends AnyWordSpec {

  class DummyFileIO extends FileIOInterface {
    override def save(board: de.htwg.model.BoardInterface): Unit = {}

    override def load(): de.htwg.model.BoardInterface = new Board(2, 1)
  }

  "A board's strategy" should {
    "be able to create an easy board" in {
      GameConfig.getInstance.setCustom(6, 5)
      val controller = new Controller(BoardFactory.getInstance, new DummyFileIO())
      val board = controller.getBoard

      board.size should be (6)
      board.mineCount should be (5)
    }
    "be able to create a medium board" in {
      GameConfig.getInstance.setCustom(9, 15)
      val controller = new Controller(BoardFactory.getInstance, new DummyFileIO())
      val board = controller.getBoard

      board.size should be(9)
      board.mineCount should be(15)
  }
    "be able to create a hard board" in {
      GameConfig.getInstance.setCustom(12, 35)
      val controller = new Controller(BoardFactory.getInstance, new DummyFileIO())
      val board = controller.getBoard

      board.size should be(12)
      board.mineCount should be(35)
    }
    "be able to create a custom sized board" in {
      GameConfig.getInstance.setCustom(13, 55)
      val controller = new Controller(BoardFactory.getInstance, new DummyFileIO())
      val board = controller.getBoard

      board.size should be(13)
      board.mineCount should be(55)
    }

    "return the BoardFactory singleton from getBoardFactory" in {
      val factoryFromStrategy = CustomStrategy.getBoardFactory()
      val factoryDirect = BoardFactory.getInstance

      factoryFromStrategy should not be null
      factoryFromStrategy shouldBe factoryDirect
    }

  }
}
