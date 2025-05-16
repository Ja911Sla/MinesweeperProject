package de.htwg.factory

import de.htwg.model.Board
import de.htwg.singleton.GameConfig

trait BoardFactory {
  def size: Int

  def mineCount: Int

  def createBoard(): Board = {
    val board = new Board(size, mineCount)
    board.placeMines()
    board
  }
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

object ConfigBoardFactory extends BoardFactory {
  override def size: Int = GameConfig.boardSize

  override def mineCount: Int = GameConfig.mineCount
}
