package de.htwg.controller.controllerBase

import de.htwg.controller.ControllerInterface
import de.htwg.factory.{BoardFactory, BoardFactoryInterface}
import de.htwg.model.*
import de.htwg.model.boardBase.{Board, Timer}
import de.htwg.utility.Observable

import scala.collection.mutable
import scala.util.Try


class Controller(private var boardFactory: BoardFactory)
  extends ControllerInterface with Observable {
  //class Controller(private var boardFactory: BoardFactory) extends ControllerInterface, Observable {
  private var board: BoardInterface = boardFactory.createBoard()
  private val timer = new Timer()
  private val undoStack: mutable.Stack[Command] = mutable.Stack() // andu Schtäck
  private val redoStack: mutable.Stack[Command] = mutable.Stack() // redu Schtäck
  var isGameOver: Boolean = false
  var isWon: Boolean = false

  def getBoard: BoardInterface = board

  def setBoard(storedBoard: BoardInterface): Unit = {
    board = storedBoard
    notifyObservers()
  }

  def getElapsedTime: Int = timer.getTime

  var isDifficultySet: Boolean = false
  def createNewBoard(factory: BoardFactory): Unit = {
    boardFactory = factory
    board = boardFactory.createBoard()
    isDifficultySet = true
    notifyObservers()
  }

  def revealCell(row: Int, col: Int): Boolean = {
    val safe = board.reveal(row, col)
    if (!safe) {
      isGameOver = true
    } else if (checkWin()) {
      isWon = true }
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

  override def copyBoard(): BoardInterface = {
    val storedBoard = board.copyBoard()
    storedBoard
  }

  def doAndStore(cmd: Command): Unit = {
    cmd.doStep()
    undoStack.push(cmd)
    redoStack.clear()
    notifyObservers()
  }

  def undo(): Unit = {
    Try(undoStack.pop()).map { cmd =>
      cmd.undoStep()
      redoStack.push(cmd)
      notifyObservers()
    }.getOrElse {
      println("Bereits am Anfang des Spiels – nichts mehr rückgängig zu machen.")
    }
  }

  def redo(): Unit = {
    Try(redoStack.pop()).map { cmd =>
      cmd.redoStep()
      undoStack.push(cmd)
      notifyObservers()
    }.getOrElse {
      println("Du bist bereits im aktuellen Spielstand – nichts zum Wiederholen.")
    }
  }
  
  
  def remainingFlags(): Int = board.remainingFlags()

  
  def undoStackSize: Int = undoStack.size
  def redoStackSize: Int = redoStack.size


  def getUndoStack: mutable.Stack[Command] = undoStack

  def getRedoStack: mutable.Stack[Command] = redoStack


}
