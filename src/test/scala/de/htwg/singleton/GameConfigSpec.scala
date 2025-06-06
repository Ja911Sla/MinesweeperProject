package de.htwg.singleton

import de.htwg.controller.controllerBase.Controller
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import scala.Console
import de.htwg.factory.{BoardFactory, ConfigBoardFactory}
import de.htwg.model.boardBase.Board
import de.htwg.singleton.GameConfig

class GameConfigSpec extends AnyWordSpec {
    "A board's configuration" should {
      "have default values set" in {
        GameConfig.getInstance.setCustom(9, 15)
        val controller = new Controller(BoardFactory.getInstance)
        GameConfig.getInstance.reset
        GameConfig.getInstance.boardSize should be (9)
        GameConfig.getInstance.mineCount should be (10)
      }
      "have an easy size" in {
        GameConfig.getInstance.setCustom(6, 5)
        val controller = new Controller(BoardFactory.getInstance)
        controller.getBoard.size should be (6)
        controller.getBoard.mineCount should be (5)
      }
      "have a medium size" in {
        GameConfig.getInstance.setCustom(9, 15)
        val controller = new Controller(BoardFactory.getInstance)
        controller.getBoard.size should be (9)
        controller.getBoard.mineCount should be (15)
      }
      "have a big size" in {
        GameConfig.getInstance.setCustom(12, 35)
        val controller = new Controller(BoardFactory.getInstance)
        controller.getBoard.size should be(12)
        controller.getBoard.mineCount should be(35)
      }
      "have a custom size" in {
        GameConfig.getInstance.setCustom(8, 25)
        val controller = new Controller(BoardFactory.getInstance)
        controller.getBoard.size should be(8)
        controller.getBoard.mineCount should be(25)
      }
      "have a reset function" in {
        GameConfig.getInstance.setCustom(8, 25)
        GameConfig.getInstance.reset
        GameConfig.getInstance.boardSize should be (9)
        GameConfig.getInstance.mineCount should be (10)
      }
      "GameConfig.setCustom" should {
        "accept valid size and mine values" in {
          GameConfig.getInstance.setCustom(10, 20)
          GameConfig.getInstance.boardSize should be(10)
          GameConfig.getInstance.mineCount should be(20)
        }

        "reset to default when size is too small" in {
          GameConfig.getInstance.setCustom(1, 5)
          GameConfig.getInstance.boardSize should be(9)
          GameConfig.getInstance.mineCount should be(15)
        }

        "reset to default when size is too big" in {
          GameConfig.getInstance.setCustom(27, 5)
          GameConfig.getInstance.boardSize should be(9)
          GameConfig.getInstance.mineCount should be(15)
        }

        "reset to default when mines are too few" in {
          GameConfig.getInstance.setCustom(10, 0)
          GameConfig.getInstance.boardSize should be(9)
          GameConfig.getInstance.mineCount should be(15)
        }

        "reset to default when mines exceed board capacity" in {
          GameConfig.getInstance.setCustom(3, 9)
          GameConfig.getInstance.boardSize should be(9)
          GameConfig.getInstance.mineCount should be(15)
        }
      }
    }
}
