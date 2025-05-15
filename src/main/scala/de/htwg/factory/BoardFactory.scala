package de.htwg.factory

import de.htwg.model.Board
import de.htwg.singleton.GameConfig

trait BoardFactory {
  def createBoard(): Board
}

object EasyBoardFactory extends BoardFactory {
  override def createBoard(): Board = new Board(size = 6, mineCount = 5)
}

object MediumBoardFactory extends BoardFactory {
  override def createBoard(): Board = new Board(size = 9, mineCount = 15)
}

object HardBoardFactory extends BoardFactory {
  override def createBoard(): Board = new Board(size = 12, mineCount = 35)
}

object ConfigBoardFactory extends BoardFactory {
  override def createBoard(): Board =
    new Board(GameConfig.boardSize, GameConfig.mineCount)
}





