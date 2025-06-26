package de.htwg.model.singleton

trait GameConfigInterface {
  def boardSize: Int
  def mineCount: Int
  def setCustom(size: Int, mines: Int): GameConfigInterface
  def reset: GameConfigInterface
}

