package de.htwg.controller.controllerBase

import de.htwg.model.BoardInterface
import de.htwg.model.boardBase.Board
import de.htwg.controller.ControllerInterface

trait Command {
  def doStep(): Unit
  def undoStep(): Unit
  def redoStep(): Unit
}

class SetCommand(row: Int, col: Int, controller: ControllerInterface) extends Command {
  private val oldBoard: BoardInterface = controller.copyBoard()
  private var newBoard: Option[BoardInterface] = None

  override def doStep(): Unit = {
    controller.revealCell(row, col)
    newBoard = Some(controller.copyBoard()) // den neuen Zustand nach der Änderung speichern
  }

  override def undoStep(): Unit = {
    controller.setBoard(oldBoard)
  }

  override def redoStep(): Unit = {
    newBoard match {
      case Some(b) => controller.setBoard(b)
      case None => println("Kein Redo-Zustand vorhanden.")
    }
  }
}
class FlagCommand(row: Int, col: Int, controller: ControllerInterface) extends Command {
  private val oldBoard: BoardInterface = controller.copyBoard()
  private var newBoard: Option[BoardInterface] = None

  override def doStep(): Unit = {
    controller.flagCell(row, col)
    newBoard = Some(controller.copyBoard())
  }

  override def undoStep(): Unit = {
    controller.setBoard(oldBoard)
  }

  override def redoStep(): Unit = {
    newBoard match {
      case Some(b) => controller.setBoard(b)
      case None => println("Kein Redo-Zustand für Flag vorhanden.")
    }
  }
}
