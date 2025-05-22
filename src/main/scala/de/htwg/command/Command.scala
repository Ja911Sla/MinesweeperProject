package de.htwg.command

import de.htwg.controller.Controller
import de.htwg.model.Board


trait Command {
  def doStep(): Unit
  def undoStep(): Unit
  def redoStep(): Unit
}

class SetCommand(row:Int, col: Int, value:Int, controller: Controller) extends Command {
  private val oldBoard: Board = controller.getBoard.copy()
  
  override def doStep(): Unit = controller.revealCell(row, col)

  override def undoStep(): Unit = controller.setBoard(oldBoard)

  override def redoStep(): Unit = controller.revealCell(row, col)
}
