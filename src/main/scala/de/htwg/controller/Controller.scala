package de.htwg.controller

import de.htwg.utility.Observable
import de.htwg.model._
import de.htwg.factory.BoardFactory
import de.htwg.command.Command
import scala.collection.mutable // veränderbare Datenstrukturen


class Controller(private var boardFactory: BoardFactory) extends Observable {
  private var board: Board = boardFactory.createBoard()
  private val timer = new Timer()
  private val commandStack: mutable.Stack[Command] = mutable.Stack() // Schtäck für Undo

  def getBoard: Board = board

  def setBoard(storedBoard: Board): Unit = {
    board = storedBoard
    notifyObservers()
  }

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

  def copyBoard(): Board = {
    val storedBoard = board.copyBoard()
    storedBoard
  }

  def doAndStore(cmd: Command): Unit = {
    cmd.doStep() // irgendein Befehl ausführen
    commandStack.push(cmd) // Befehl auf den Stack legen
  }
  // Macht den letzten Spielzug rückgängig
  def undo(): Unit = {
    if (commandStack.nonEmpty) {
      val lastCommand = commandStack.pop()
      lastCommand.undoStep()
      timer.start() // Timer fortsetzen nach Undo
      notifyObservers()
    } else {
      println("Nichts zum Rückgängig machen.")
    }
  }

}
