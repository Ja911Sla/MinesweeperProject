package de.htwg.controller

import de.htwg.utility.Observable
import de.htwg.model._

class Controller(var board:Board) extends Observable {      // Controller soll koordinieren und keine Duplikation sein
  def createNewBoard(size: Int, amountMines: Int): Unit = {
    board = new Board(size, amountMines)
    notifyObservers
  }

  def revealCell(row: Int, col: Int): Boolean = {
    val safe = board.reveal(row, col)
    notifyObservers
    safe
  }

  def flagCell(x: Int, y: Int): Unit = {
    board.toggleFlag(x, y)
    notifyObservers
  }

  def checkWin(): Boolean = {
    val result = board.checkWin()
    notifyObservers
    result
  }

  def resetGame(): Unit = {
    board.reset()
    notifyObservers
  }

  def displayBoard(revealAll: Boolean = false): Unit = {
    board.display(revealAll)
  }
}


