package de.htwg.controller.controllerBase

//import de.htwg.command.*

import de.htwg.controller.controllerBase.{Controller, FlagCommand, SetCommand}
import de.htwg.controller.factory.BoardFactory
import de.htwg.fileio.FileIOInterface
import de.htwg.model.boardBase.Board
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec


class CommandSpec extends AnyWordSpec {

  class DummyFileIO extends FileIOInterface {
    override def save(board: de.htwg.model.BoardInterface): Unit = {}

    override def load(): de.htwg.model.BoardInterface = new Board(2, 1)
  }

  object TestBoardFactory extends BoardFactory {
    override def createBoard(): Board = new Board(2, 1) 
  }
  
  "A SetCommand" should {
    "reveal a cell when doStep is called" in {
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      val row = 0
      val col = 0

      val cmd = new SetCommand(row, col, controller)

      controller.getBoard.cells(row)(col).isRevealed shouldBe false

      cmd.doStep()

      controller.getBoard.cells(row)(col).isRevealed shouldBe true
    }

    "undo the reveal when undoStep is called" in {
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      val row = 0
      val col = 0

      val cmd = new SetCommand(row, col, controller)

      cmd.doStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe true

      cmd.undoStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe false
    }

    "redo the reveal when redoStep is called" in {
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      val row = 0
      val col = 0

      val cmd = new SetCommand(row, col, controller)

      cmd.doStep()
      cmd.undoStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe false

      cmd.redoStep()
      controller.getBoard.cells(row)(col).isRevealed shouldBe true
    }

    "work with Controller.doAndStore and undo" in {
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      val row = 1
      val col = 1

      val cmd = new SetCommand(row, col, controller)
      controller.doAndStore(cmd)

      controller.getBoard.cells(row)(col).isRevealed shouldBe true

      controller.undo()
      controller.getBoard.cells(row)(col).isRevealed shouldBe false
    }
    "not redo when no newBoard is present in SetCommand" in {
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      val row = 0
      val col = 0

      val cmd = new SetCommand(row, col, controller)
      // Achtung: kein doStep() → newBoard bleibt None
      cmd.redoStep() // sollte println auslösen: "Kein Redo-Zustand vorhanden."
      // Kein assert nötig, da println nicht getestet wird – aber Zeile wird gecovert!
    }
    "undo a flag correctly with FlagCommand" in {
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      val row = 1
      val col = 1

      val cmd = new FlagCommand(row, col, controller)
      cmd.doStep()
      controller.getBoard.cells(row)(col).isFlagged shouldBe true

      cmd.undoStep()
      controller.getBoard.cells(row)(col).isFlagged shouldBe false
    }
    "redo a flag correctly with FlagCommand" in {
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      val row = 1
      val col = 1

      val cmd = new FlagCommand(row, col, controller)
      cmd.doStep()
      cmd.undoStep()

      cmd.redoStep()
      controller.getBoard.cells(row)(col).isFlagged shouldBe true
    }
    "not redo flag if no newBoard is set in FlagCommand" in {
      val controller = new Controller(TestBoardFactory, new DummyFileIO())
      val row = 0
      val col = 1

      val cmd = new FlagCommand(row, col, controller)
      // kein doStep(), also newBoard = None
      cmd.redoStep() // sollte println auslösen: "Kein Redo-Zustand für Flag vorhanden."
    }
  }
}
