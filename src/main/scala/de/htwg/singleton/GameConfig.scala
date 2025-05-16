package de.htwg.singleton

object GameConfig {
  private var _boardSize: Int = 9
  private var _mineCount: Int = 15

  def boardSize: Int = _boardSize
  def mineCount: Int = _mineCount

  def setEasy: GameConfig.type = {
    _boardSize = 6
    _mineCount = 5
    this
  }

  def setMedium: GameConfig.type = {
    _boardSize = 9
    _mineCount = 15
    this
  }

  def setHard: GameConfig.type = {
    _boardSize = 12
    _mineCount = 35
    this
  }

  def setCustom(size: Int, mines: Int): GameConfig.type = {
    _boardSize = size
    _mineCount = mines
    this
  }

  def reset: GameConfig.type = {
    _boardSize = 9
    _mineCount = 10
    this
  }
}