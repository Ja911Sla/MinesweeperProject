package de.htwg.factory

import de.htwg.model.Board
import de.htwg.singleton.GameConfig

trait IBoardFactory {
  def createBoard(): Board
}

