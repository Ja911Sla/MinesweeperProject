package de.htwg.model

trait BoardInterface {
  def reset(): String
  def placeMines(): Int
  def reveal(row: Int, col: Int): Boolean
  def toggleFlag(row: Int, col: Int): Unit
  def checkWin(): Boolean
  def display(revealAll: Boolean = false): String
  def bombCountDisplayString(): String
  def copyBoard(): BoardInterface
  def remainingFlags(): Int
  def countAdjacentMines(row: Int, col: Int): Int
}

