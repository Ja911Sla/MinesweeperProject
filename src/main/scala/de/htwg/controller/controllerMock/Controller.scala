package de.htwg.controller.controllerMock

import de.htwg.controller.ControllerInterface
import de.htwg.controller.controllerBase.Command
import de.htwg.controller.factory.BoardFactory
import de.htwg.utility.*
import de.htwg.model.BoardInterface
import de.htwg.model.boardBase.Board
import scala.collection.mutable
import de.htwg.model.boardBase.Timer
import de.htwg.utility.Observable

class Controller(private var boardFactory: BoardFactory) extends ControllerInterface with Observable {
  private var board: BoardInterface = boardFactory.createBoard()
  private val timer = new Timer()
  private val undoStack: mutable.Stack[Command] = mutable.Stack()
  private val redoStack: mutable.Stack[Command] = mutable.Stack()
  private var observers: List[Observer] = List.empty
  var revealed: Boolean = false
  var flagged: Boolean = false
  var resetCalled: Boolean = false

  override def isGameOver: Boolean = false // Mock implementation

  override def isWon: Boolean = false // Mock implementation

  override def getBoard: BoardInterface = board

  override def setBoard(storedBoard: BoardInterface): Unit = {
    board = storedBoard
    notifyObservers() // Now accessible
  }

  /*
    override def setBoard(newBoard: BoardInterface): Unit = {
      board = newBoard
    }*/

  override def isDifficultySet: Boolean = false

  override def setDifficultySet(flag: Boolean): Unit = {}



  override def createNewBoard(factory: BoardFactory): Unit = {
    board = factory.createBoard()
  }

  override def revealCell(row: Int, col: Int): Boolean = {
    revealed = true
    !(row == 0 && col == 0) // simulate mine at (0,0)
  }

  override def flagCell(row: Int, col: Int): Unit = {
    flagged = !flagged
  }

  override def checkWin(): Boolean = revealed

  override def resetGame(): String = {
    resetCalled = true
    "Mock: Game reset"
  }

  override def displayBoardToString(revealAll: Boolean = false): String = {
    if (revealAll) "Mock Display All"
    else "Mock Display"
  }

  override def copyBoard(): BoardInterface = new Board()

  override def doAndStore(cmd: Command): Unit = {

  }

  override def undo(): Unit = {

  }

  override def redo(): Unit = {

  }

  override def remainingFlags(): Int = 1

  override def getElapsedTime: Int = 42

  override def undoStackSize: Int = 0

  override def redoStackSize: Int = 0

  override def add(observer: Observer): Unit = {
    observers = observer :: observers
  }

  override def remove(observer: Observer): Unit = {
    observers = observers.filterNot(_ ==observer)
    }

  override def getUndoStack: mutable.Stack[Command] = undoStack
  override def getRedoStack: mutable.Stack[Command] = redoStack

  override def save(): Unit = ???

  override def load(): Unit = ???
}