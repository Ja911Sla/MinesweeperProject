package de.htwg.controller.factory

import de.htwg.model.boardBase.Board
import de.htwg.model.singleton.GameConfig

trait BoardFactoryInterface {
  def createBoard(): Board
}

