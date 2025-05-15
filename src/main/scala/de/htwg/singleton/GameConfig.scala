package de.htwg.singleton

object GameConfig {
  var boardSize: Int = 9
  var mineCount: Int = 10

  def setEasy(): Unit = {
    boardSize = 6
    mineCount = 5
  }

  def setMedium(): Unit = {
    boardSize = 9
    mineCount = 15
  }

  def setHard(): Unit = {
    boardSize = 12
    mineCount = 35
  }

  def setCustom(size: Int, mines: Int): Unit = {
    boardSize = size
    mineCount = mines
  }

  def reset(): Unit = {
    boardSize = 9
    mineCount = 10
  }
}

