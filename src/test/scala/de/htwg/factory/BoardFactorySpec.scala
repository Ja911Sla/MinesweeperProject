package de.htwg.factory

import de.htwg.controller.controllerBase.Controller
import de.htwg.model.boardBase.Board
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import scala.Console
import de.htwg.singleton.GameConfig


import java.io.*


class BoardFactorySpec extends AnyWordSpec {
  "A Board" should {
    "have an easy mode" in {
      GameConfig.getInstance.setCustom(6, 5)
      val controller = new Controller(BoardFactory.getInstance)
      val board = controller.getBoard
      
      board.size should be (6)
      board.mineCount should be (5)
    }
    "have a medium mode" in {
      GameConfig.getInstance.setCustom(9, 15)
      val controller = new Controller(BoardFactory.getInstance)
      val board = controller.getBoard

      board.size should be(9)
      board.mineCount should be(15)
    }
    "have a hard mode" in {
      GameConfig.getInstance.setCustom(12, 35)
      val controller = new Controller(BoardFactory.getInstance)
      val board = controller.getBoard

      board.size should be(12)
      board.mineCount should be(35)
    }
    "have a user friendly creation mode" in {
      GameConfig.getInstance.setCustom(9, 15)
      val controller = new Controller(BoardFactory.getInstance)
      val board = controller.getBoard
      
      board.size should be (9)
      board.mineCount should be (15)
      
    }
    "EasyBoardFactory" should {
      "create a 6x6 board with 5 mines" in {
        val board: Board = EasyBoardFactory.createBoard()
        board.size shouldBe 6
        board.mineCount shouldBe 5
      }
    }

    "MediumBoardFactory" should {
      "create a 9x9 board with 15 mines" in {
        val board: Board = MediumBoardFactory.createBoard()
        board.size shouldBe 9
        board.mineCount shouldBe 15
      }
    }

    "HardBoardFactory" should {
      "create a 12x12 board with 35 mines" in {
        val board: Board = HardBoardFactory.createBoard()
        board.size shouldBe 12
        board.mineCount shouldBe 35
      }
    }
  }
}
