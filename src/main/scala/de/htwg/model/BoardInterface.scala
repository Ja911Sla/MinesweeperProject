package de.htwg.model
import de.htwg.model.boardBase.GameCell


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
  def revealAdjacent(row: Int, col: Int): Unit
  def inBounds(row: Int, col: Int):Boolean
  def cells: Array[Array[GameCell]]
  def size: Int

}

