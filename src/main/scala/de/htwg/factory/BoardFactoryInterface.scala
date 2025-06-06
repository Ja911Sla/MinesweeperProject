package de.htwg.factory

import de.htwg.model.boardBase.Board
import de.htwg.singleton.GameConfig

trait BoardFactoryInterface {
  def createBoard(): Board
}

