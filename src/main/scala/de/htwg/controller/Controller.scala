package de.htwg.controller

import de.htwg.utility.Observable
import de.htwg.model._

class Controller(var board:Board) extends Observable {      // Controller soll koordinieren und keine Duplikation sein
  def createNewBoard(size: Int, amountMines: Int): Unit = {
    board = new Board(size, amountMines)
    notifyObservers
  }

  def revealCell(x: Int, y: Int): Unit = {
    board.reveal(x, y)
    notifyObservers
  }

  def flagCell(x: Int, y: Int): Unit = {
    board.toggleFlag(x, y)
    notifyObservers
  }

  def checkWin(): Boolean = board.checkWin()


  def resetGame(): Unit = {
    board.reset()
    notifyObservers
  }
}


