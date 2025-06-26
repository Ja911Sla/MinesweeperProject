package de.htwg.controller.controllerBase

import de.htwg.controller.ControllerInterface
import de.htwg.model.*
import de.htwg.model.boardBase.{Board, Timer}
import de.htwg.utility.Observable

import scala.collection.mutable
import scala.util.Try
import de.htwg.controller.factory.{BoardFactory, BoardFactoryInterface}
import de.htwg.model.BoardInterface
import de.htwg.utility.Observer
import de.htwg.fileio.{FileIOInterface, FileIOJson}

class Controller(private var boardFactory: BoardFactory, val fileIO: FileIOInterface)
  extends ControllerInterface with Observable {
  //class Controller(private var boardFactory: BoardFactory) extends ControllerInterface, Observable {
  private var board: BoardInterface = boardFactory.createBoard()
  private val timer = new Timer()
  private val undoStack: mutable.Stack[Command] = mutable.Stack() // andu Schtäck
  private val redoStack: mutable.Stack[Command] = mutable.Stack() // redu Schtäck
  var isGameOver: Boolean = false
  var isWon: Boolean = false

  //val fileIO0: FileIOInterface = new FileIOJson()

  override def getBoard: BoardInterface = board

  override def setBoard(storedBoard: BoardInterface): Unit = {
    board = storedBoard
    notifyObservers()
  }

  override def getElapsedTime: Int = timer.getTime

  private var _isDifficultySet: Boolean = false

  override def isDifficultySet: Boolean = _isDifficultySet

  override def setDifficultySet(flag: Boolean): Unit = {
    _isDifficultySet = flag
  }

  override def createNewBoard(factory: BoardFactory): Unit = {
    boardFactory = factory
    board = boardFactory.createBoard()
    _isDifficultySet = true
    notifyObservers()
  }

  override def revealCell(row: Int, col: Int): Boolean = {
    val safe = board.reveal(row, col)
    if (!safe) {
      isGameOver = true
    } else if (checkWin()) {
      isWon = true
    }
    notifyObservers()
    safe
  }


  override def flagCell(x: Int, y: Int): Unit = {
    board.toggleFlag(x, y)
    notifyObservers()
  }

  override def checkWin(): Boolean = {
    val result = board.checkWin()
    if (result) timer.stop()
    notifyObservers()
    result
  }

  override def resetGame(): String = {
    timer.reset()
    val resetMessage = board.reset()
    timer.start()
    notifyObservers()
    resetMessage
  }

  override def displayBoardToString(revealAll: Boolean = false): String = {
    board.display(revealAll)
  }

  override def copyBoard(): BoardInterface = {
    val storedBoard = board.copyBoard()
    storedBoard
  }

  override def doAndStore(cmd: Command): Unit = {
    cmd.doStep()
    undoStack.push(cmd)
    redoStack.clear()
    notifyObservers()
  }

  override def undo(): Unit = {
    Try(undoStack.pop()).map { cmd =>
      cmd.undoStep()
      redoStack.push(cmd)
      notifyObservers()
    }.getOrElse {
      println("Bereits am Anfang des Spiels – nichts mehr rückgängig zu machen.")
    }
  }

  override def redo(): Unit = {
    Try(redoStack.pop()).map { cmd =>
      cmd.redoStep()
      undoStack.push(cmd)
      notifyObservers()
    }.getOrElse {
      println("Du bist bereits im aktuellen Spielstand – nichts zum Wiederholen.")
    }
  }


  override def remainingFlags(): Int = board.remainingFlags()


  override def undoStackSize: Int = undoStack.size

  override def redoStackSize: Int = redoStack.size


  override def getUndoStack: mutable.Stack[Command] = undoStack

  override def getRedoStack: mutable.Stack[Command] = redoStack
 // new
  override def add(observer: Observer): Unit = super.add(observer)

  override def remove(observer: Observer): Unit = super.remove(observer)

  def save(): Unit = fileIO.save(board)

  def load(): Unit = {
    board = fileIO.load()
    notifyObservers()
  }

}
// Die zentrale given-Instanz
given ControllerInterface = new Controller(BoardFactory.getInstance, new FileIOJson())
