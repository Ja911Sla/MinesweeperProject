package de.htwg.model.boardMock

import de.htwg.model.BoardInterface
import de.htwg.model.*
import de.htwg.model.boardBase.GameCell

class Board() extends BoardInterface {
  val size: Int = 9
  val mineCount: Int = 10
  val cells: Array[Array[GameCell]] = Array.fill(size, size)(GameCell())

  override def reset(): String = {
    "Mock: Board reset."
  }

  override def placeMines(): Int = {
    1 // Immer eine Mine für Test
  }

  override def reveal(row: Int, col: Int): Boolean = {
    true // Immer erfolgreich
  }

  override def inBounds(row: Int, col: Int): Boolean = {
    row >= 0 && row < size && col >= 0 && col < size
  }

  override def countAdjacentMines(row: Int, col: Int): Int = {
    0 // Für Tests: keine benachbarten Minen
  }

  override def revealAdjacent(row: Int, col: Int): Unit = {
    // Tut nichts
  }

  override def toggleFlag(row: Int, col: Int): Unit = {
    cells(row)(col).isFlagged = !cells(row)(col).isFlagged
  }

  override def checkWin(): Boolean = {
    true // Testweise immer gewonnen
  }

  override def display(revealAll: Boolean): String = {
    "Mock: Board Display"
  }

  override def bombCountDisplayString(): String = {
    "Mock: Bomb Count"
  }

  override def copyBoard(): BoardInterface = {
    new Board() // Gibt ein neues Mock-Board zurück
  }

  override def remainingFlags(): Int={
    0
    }
}