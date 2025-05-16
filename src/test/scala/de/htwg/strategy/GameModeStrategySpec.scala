package de.htwg.strategy


import de.htwg.model.Board
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import scala.Console
import de.htwg.controller.Controller
import de.htwg.factory._
import de.htwg.singleton.GameConfig

class GameModeStrategySpec extends AnyWordSpec {
  "A board's strategy" should {
    "be able to create an easy board" in {
      val controller = new Controller(EasyBoardFactory)
      val board = controller.getBoard

      board.size should be (6)
      board.mineCount should be (5)
    }
    "be able to create a medium board" in {
      val controller = new Controller(MediumBoardFactory)
      val board = controller.getBoard

      board.size should be(9)
      board.mineCount should be(13)
  }
    "be able to create a hard board" in {
      val controller = new Controller(HardBoardFactory)
      val board = controller.getBoard

      board.size should be(12)
      board.mineCount should be(35)
    }
    "be able to create a custom sized board" in {
      GameConfig.setCustom(13, 55)
      val controller = new Controller(ConfigBoardFactory)
      val board = controller.getBoard

      board.size should be(13)
      board.mineCount should be(55)
    }
  }
}
