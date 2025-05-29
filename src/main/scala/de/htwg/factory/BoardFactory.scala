package de.htwg.factory

import de.htwg.model.Board
import de.htwg.singleton.GameConfig

trait BoardFactory {
  //def size: Int

  //def mineCount: Int

  def createBoard(): Board
}

object EasyBoardFactory extends BoardFactory {
  override val size: Int = 6
  override val mineCount: Int = 5
}

object MediumBoardFactory extends BoardFactory {
  override val size: Int = 9
  override val mineCount: Int = 15
}

object HardBoardFactory extends BoardFactory {

  override val size: Int = 12
  override val mineCount: Int = 35
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