package de.htwg.singleton

trait IGameConfig {
  def boardSize: Int
  def mineCount: Int
  def setCustom(size: Int, mines: Int): IGameConfig
  def reset: IGameConfig
}

