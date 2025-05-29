package de.htwg.factory

import de.htwg.model.Board
import de.htwg.singleton.GameConfig

trait BoardFactory {
  def createBoard(): Board
}

object EasyBoardFactory extends BoardFactory {
  override def createBoard(): Board = {
    val board = new Board(6, 5)
    board.placeMines()
    board
  }
}

object MediumBoardFactory extends BoardFactory {
  override def createBoard(): Board = {
    val board = new Board(9, 15)
    board.placeMines()
    board
  }
}

object HardBoardFactory extends BoardFactory {
  override def createBoard(): Board = {
    val board = new Board(12, 35)
    board.placeMines()
    board
  }
}

private class ConfigBoardFactory extends BoardFactory {
  override def createBoard(): Board = {
    val config = GameConfig.getInstance
    val board = new Board(config.boardSize, config.mineCount)
    board.placeMines()
    board
  }
}

object BoardFactory {
  private val factory = new ConfigBoardFactory()

  def getInstance: BoardFactory = factory
}
