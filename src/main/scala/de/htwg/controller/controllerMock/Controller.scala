
package de.htwg.controller.controllerMock


import de.htwg.controller.ControllerInterface
import de.htwg.controller.controllerBase.Command
import de.htwg.utility.*
import de.htwg.model.BoardInterface
import de.htwg.factory.*
import de.htwg.model.boardBase.Board

class Controller(private var boardFactory: BoardFactory) extends ControllerInterface {
  private var board: BoardInterface = new Board()
  private var observers: List[Observer] = List.empty
  var revealed: Boolean = false
  var flagged: Boolean = false
  var resetCalled: Boolean = false

  override def getBoard: BoardInterface = board

  override def setBoard(newBoard: BoardInterface): Unit = {
    board = newBoard
  }

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
}