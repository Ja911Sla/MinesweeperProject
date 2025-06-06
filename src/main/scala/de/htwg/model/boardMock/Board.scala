//package de.htwg.model.boardMock
//
//package de.htwg.model.boardBase
//
//import de.htwg.model.BoardInterface
//import de.htwg.model._
//
//class Board extends BoardInterface {
//  val size: Int = 3
//  val mineCount: Int = 1
//  val cells: Array[Array[GameCell]] = Array.fill(size, size)(GameCell())
//
//  override def reset(): String = {
//    "Mock: Board reset."
//  }
//
//  override def placeMines(): Int = {
//    1 // Immer eine Mine für Test
//  }
//
//  override def reveal(row: Int, col: Int): Boolean = {
//    true // Immer erfolgreich
//  }
//
//  override def inBounds(r: Int, c: Int): Boolean = {
//    r >= 0 && r < size && c >= 0 && c < size
//  }
//
//  override def countAdjacentMines(row: Int, col: Int): Int = {
//    0 // Für Tests: keine benachbarten Minen
//  }
//
//  override def revealAdjacent(row: Int, col: Int): Unit = {
//    // Tut nichts
//  }
//
//  override def toggleFlag(row: Int, col: Int): Unit = {
//    cells(row)(col).isFlagged = !cells(row)(col).isFlagged
//  }
//
//  override def checkWin(): Boolean = {
//    true // Testweise immer gewonnen
//  }
//
//  override def display(revealAll: Boolean = false): String = {
//    "Mock: Board Display"
//  }
//
//  override def bombCountDisplayString(): String = {
//    "Mock: Bomb Count"
//  }
//
//  override def copyBoard(): Board = {
//    new Board(size, mineCount) // Gibt ein leeres echtes Board zurück
//  }
//
//  override def remainingFlags(): Int = {
//    0
//  }
//}
//
