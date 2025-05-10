package de.htwg.controller

import de.htwg.utility.Observable
import de.htwg.model._

class Controller(var board:Board) extends Observable {      // Controller soll koordinieren und keine Duplikation sein
  private val timer = new Timer()
  def getElapsedTime: Int = timer.getTime
  def createNewBoard(size: Int, amountMines: Int): Unit = {
    board = new Board(size, amountMines)
    notifyObservers()
  }

  def revealCell(row: Int, col: Int): Boolean = {
    val safe = board.reveal(row, col)
    notifyObservers()
    safe
  }

  def flagCell(x: Int, y: Int): Unit = {
    board.toggleFlag(x, y)
    notifyObservers()
  }

  def checkWin(): Boolean = {
    val result = board.checkWin()
    if (result) timer.stop()  // Only stop if won
    notifyObservers()
    result
  }

  def resetGame(): String = {
    timer.reset()  // Restart the timer cleanlyA
    val resetMessage = board.reset()
    timer.start()  // Ensure it starts again if stopped
    notifyObservers()
    resetMessage
  }
  /*
  def displayBoard(revealAll: Boolean = false): Unit = {
    board.display(revealAll)
  }*/

  def displayBoardToString(revealAll: Boolean = false): String = {
    board.display(revealAll)
  }
}


