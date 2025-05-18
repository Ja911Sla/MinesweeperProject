package de.htwg.controller

import de.htwg.utility.Observable
import de.htwg.model._
import de.htwg.factory.BoardFactory

class Controller(private var boardFactory: BoardFactory) extends Observable {
  private var board: Board = boardFactory.createBoard()
  private val timer = new Timer()

  def getBoard: Board = board

  def getElapsedTime: Int = timer.getTime

  def createNewBoard(factory: BoardFactory): Unit = {
    boardFactory = factory
    board = boardFactory.createBoard()
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
    if (result) timer.stop()
    notifyObservers()
    result
  }

  def resetGame(): String = {
    timer.reset()
    val resetMessage = board.reset()
    timer.start()
    notifyObservers()
    resetMessage
  }

  def displayBoardToString(revealAll: Boolean = false): String = {
    board.display(revealAll)
  }

}
