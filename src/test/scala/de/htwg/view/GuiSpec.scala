package de.htwg.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.controller.Controller
import de.htwg.factory.BoardFactory
import de.htwg.model.Board
import de.htwg.view.Gui

class GuiSpec extends AnyWordSpec {

  object TestBoardFactory extends BoardFactory {
    override def createBoard(): Board = new Board(6, 5)
    //override def size: Int = 6
    //override def mineCount: Int = 5
  }

  "Gui" should {
    "initialize without crashing" in {
      noException should be thrownBy {
        Gui.main(Array.empty)
      }
    }
  }
}
