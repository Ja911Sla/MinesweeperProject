package de.htwg.controller

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.controller.controllerMock.Controller
import de.htwg.controller.factory.BoardFactory


class ControllerMockSpec extends AnyWordSpec {

  class DummyFactory extends BoardFactory {
    override def createBoard() = new de.htwg.model.boardMock.Board()
  }

  "Controller's displayBoardToString" should {
    val controller: ControllerInterface = new Controller(new DummyFactory())

    "return mocked display string when revealAll is false" in {
      val output = controller.displayBoardToString()
      output should include ("Mock Display")
    }

    "return mocked full display string when revealAll is true" in {
      val output = controller.displayBoardToString(revealAll = true)
      output should include ("Mock Display All")
    }
  }
}
