package de.htwg.singleton

class GameConfig private() { //object GameConfig {
  private var _boardSize: Int = 9
  private var _mineCount: Int = 15

  def boardSize: Int = _boardSize
  def mineCount: Int = _mineCount
//
//  def setEasy: GameConfig = {
//    _boardSize = 6
//    _mineCount = 5
//    this
//  }
//
//  def setMedium: GameConfig = {
//    _boardSize = 9
//    _mineCount = 15
//    this
//  }
//
//  def setHard: GameConfig = {
//    _boardSize = 12
//    _mineCount = 35
//    this
//  }

  def setCustom(size: Int, mines: Int): GameConfig = {
    if (size < 2 || size > 26) {
      println("Ungültige Boardgröße. Erlaubt sind nur Größen von 2 bis 30. Setze auf Standard (9x9, 15 Minen).")
      _boardSize = 9
      _mineCount = 15
    } else if (mines < 1 || mines >= size * size) {
      println(s"Ungültige Minenzahl. Erlaubt sind mindestens 1 und maximal ${size * size - 1} Minen. Setze auf Standard (9x9, 15 Minen).")
      _boardSize = 9
      _mineCount = 15
    } else {
      _boardSize = size
      _mineCount = mines
    }
    this
  }

  def reset: GameConfig = {
    _boardSize = 9
    _mineCount = 10
    this
  }
}

object GameConfig {
  private var instance: GameConfig = null

  def getInstance: GameConfig = {
    if (instance == null) {
      instance = new GameConfig()
    }
    instance
  }
}