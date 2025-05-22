package de.htwg.factory

import de.htwg.model.Board
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import scala.Console
import de.htwg.controller.Controller
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
  }
}
