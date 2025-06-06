//package de.htwg.controller.controllerMock
//
//package de.htwg.model.boardBase
//
//import de.htwg.model.BoardInterface
//
//class Controller extends BoardInterface {
//  var size: Int = 5
//  var mineCount: Int = 3
//  var revealed: Boolean = false
//  var flagged: Boolean = false
//  var resetCalled: Boolean = false
//
//  override def reset(): String = {
//    resetCalled = true
//    "MockBoard: reset called"
//  }
//
//  override def placeMines(): Int = {
//    mineCount
//  }
//
//  override def reveal(row: Int, col: Int): Boolean = {
//    revealed = true
//    !(row == 0 && col == 0) // simulate mine at (0,0)
//  }
//
//  override def inBounds(r: Int, c: Int): Boolean = true
//
//  override def countAdjacentMines(row: Int, col: Int): Int = 1
//
//  override def revealAdjacent(row: Int, col: Int): Unit = {}
//
//  override def toggleFlag(row: Int, col: Int): Unit = {
//    flagged = !flagged
//  }
//
//  override def checkWin(): Boolean = revealed // simulate win if any cell is revealed
//
//  override def display(revealAll: Boolean): String = {
//    if (revealAll) "Mock Display All"
//    else "Mock Display"
//  }
//
//  override def bombCountDisplayString(): String = "Bombs left: 3"
//
//  override def copyBoard(): Board = {
//    new MockBoard().asInstanceOf[Board] // Unsafe cast to satisfy method signature
//  }
//
//  override def remainingFlags(): Int = 1
//}
//
