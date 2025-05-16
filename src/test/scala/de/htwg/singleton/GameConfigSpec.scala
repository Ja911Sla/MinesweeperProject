package de.htwg.singleton

import de.htwg.model.Board
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import scala.Console
import de.htwg.controller.Controller
import de.htwg.factory.ConfigBoardFactory
import de.htwg.singleton.GameConfig

class GameConfigSpec extends AnyWordSpec {
    "A board's configuration" should {
      "have default values set" in {
        GameConfig.reset
        GameConfig.boardSize should be (9)
        GameConfig.mineCount should be (10)
      }
      "have an easy size" in {
        GameConfig.setEasy
        val controller = new Controller(ConfigBoardFactory)
        controller.getBoard.size should be (6)
        controller.getBoard.mineCount should be (5)
      }
      "have a medium size" in {
        GameConfig.setMedium
        val controller = new Controller(ConfigBoardFactory)
        controller.getBoard.size should be (9)
        controller.getBoard.mineCount should be (15)
      }
      "have a big size" in {
        GameConfig.setHard
        val controller = new Controller(ConfigBoardFactory)
        controller.getBoard.size should be(12)
        controller.getBoard.mineCount should be(35)
      }
      "have a custom size" in {
        GameConfig.setCustom(8, 25)
        val controller = new Controller(ConfigBoardFactory)
        controller.getBoard.size should be(8)
        controller.getBoard.mineCount should be(25)
      }
      "have a reset function" in {
        GameConfig.setHard
        GameConfig.reset
        GameConfig.boardSize should be (9)
        GameConfig.mineCount should be (10)
      }
    }
}
