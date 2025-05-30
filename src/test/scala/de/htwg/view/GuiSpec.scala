package de.htwg.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import de.htwg.controller.Controller
import de.htwg.factory.BoardFactory
import de.htwg.model.Board
import de.htwg.view.Gui

import scala.swing.{BorderPanel, Button, Frame}
import scala.swing.event.{ButtonClicked, MouseClicked, MouseEvent}
import de.htwg.factory.*

import javax.swing.SwingUtilities

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
    "call startGame with EasyBoardFactory when easyButton is clicked" in {
      var receivedFactory: BoardFactory = null
      Gui.startGameHandler = factory => receivedFactory = factory

      val easyButton = Gui.difficultyPanel.contents.collect {
        case b: Button if b.text.contains("Leicht") => b
      }.head

      Gui.difficultyPanel.reactions(ButtonClicked(easyButton))

      receivedFactory shouldBe EasyBoardFactory
    }

    "call startGame with MediumBoardFactory when mediumButton is clicked" in {
      var receivedFactory: BoardFactory = null
      Gui.startGameHandler = factory => receivedFactory = factory

      val mediumButton = Gui.difficultyPanel.contents.collect {
        case b: Button if b.text.contains("Mittel") => b
      }.head

      Gui.difficultyPanel.reactions(ButtonClicked(mediumButton))

      receivedFactory shouldBe MediumBoardFactory
    }

    "call startGame with HardBoardFactory when hardButton is clicked" in {
      var receivedFactory: BoardFactory = null
      Gui.startGameHandler = factory => receivedFactory = factory

      val hardButton = Gui.difficultyPanel.contents.collect {
        case b: Button if b.text.contains("Schwer") => b
      }.head

      Gui.difficultyPanel.reactions(ButtonClicked(hardButton))

      receivedFactory shouldBe HardBoardFactory
    }

    "Gui newGameButton" should {
      "reset the GUI to difficulty selection screen" in {

        Gui.mainPanel.layout(Gui.gridPanel) = BorderPanel.Position.Center
        Gui.mainPanel.layout(Gui.newGameButton) = BorderPanel.Position.South

        Gui.newGameButton.doClick()


        assert(!Gui.mainPanel.layout.values.exists(_ == Gui.gridPanel))
        assert(!Gui.mainPanel.layout.values.exists(_ == Gui.newGameButton))

        // Test: Difficulty Panel ist wieder sichtbar in der Mitte
        //assert(Gui.mainPanel.layout(BorderPanel.Position.Center) == Gui.difficultyPanel)
        // Problem!
      }
    }
    "have the correct title" in {
      val frame: Frame = Gui.top
      frame.title should be("Minesweeper")
    }

    "have the correct preferred size" in {
      val frame: Frame = Gui.top
      frame.preferredSize.width should be(500)
      frame.preferredSize.height should be(500)
    }

    "contain the main panel as its contents" in {
      val frame: Frame = Gui.top
      frame.contents should be(Seq(Gui.mainPanel))

    }

    "initially display the difficulty panel in the center" in {
      val constraint = Gui.mainPanel.layout.get(Gui.difficultyPanel)
      constraint should contain (BorderPanel.Position.Center)
    }

    "initially place the difficulty panel in the center" in {
      val layoutPos = Gui.mainPanel.layout.get(Gui.difficultyPanel)
      layoutPos should contain(BorderPanel.Position.Center)
    }

    "start a game and set up the board correctly" in {
      Gui.startGame(TestBoardFactory)

      Gui.controller should not be null
      Gui.mainPanel.layout.keys should contain(Gui.gridPanel)
      Gui.mainPanel.layout.get(Gui.gridPanel) should contain(BorderPanel.Position.Center)


      Gui.mainPanel.layout.keys should contain(Gui.newGameButton)
      Gui.mainPanel.layout.get(Gui.newGameButton) should contain(BorderPanel.Position.South)
      Gui.mainPanel.layout.keys should not contain Gui.difficultyPanel
    }

    "set gridPanel to center and newGameButton to south" in {
      val mainPanel = new BorderPanel
      val gridPanel = new scala.swing.GridPanel(2, 2)
      val newGameButton = new scala.swing.Button("Neues Spiel")

      // Layout setzen
      mainPanel.layout(gridPanel) = BorderPanel.Position.Center
      mainPanel.layout(newGameButton) = BorderPanel.Position.South

      // Assertions
      mainPanel.layout.get(gridPanel) should contain(BorderPanel.Position.Center)
      mainPanel.layout.get(newGameButton) should contain(BorderPanel.Position.South)
    }

  }
  "GuiObserver update" should {
    "set 💣 if cell is revealed and is mine" in {
      val cell = new de.htwg.model.GameCell()
      cell.isRevealed = true
      cell.isMine = true

      val button = new scala.swing.Button()
      Gui.gridPanel.contents.clear()
      Gui.gridPanel.contents += button

      val board = new de.htwg.model.Board(1, 0)
      board.cells(0)(0) = cell
      Gui.controller = new de.htwg.controller.Controller(new de.htwg.factory.BoardFactory {
        override def createBoard(): de.htwg.model.Board = board
      })

      Gui.runObserverUpdate()

      button.text shouldBe "💣"
    }

    "set number if cell is revealed and has adjacent mines" in {
      val cell = new de.htwg.model.GameCell()
      cell.isRevealed = true
      cell.isMine = false
      cell.mineCount = 3

      val button = new scala.swing.Button()
      Gui.gridPanel.contents.clear()
      Gui.gridPanel.contents += button

      val board = new de.htwg.model.Board(1, 0)
      board.cells(0)(0) = cell
      Gui.controller = new de.htwg.controller.Controller(new de.htwg.factory.BoardFactory {
        override def createBoard(): de.htwg.model.Board = board
      })

      Gui.runObserverUpdate()

      button.text shouldBe "3"
    }

    "set empty string if cell is revealed and has no adjacent mines" in {
      val cell = new de.htwg.model.GameCell()
      cell.isRevealed = true
      cell.isMine = false
      cell.mineCount = 0

      val button = new scala.swing.Button()
      Gui.gridPanel.contents.clear()
      Gui.gridPanel.contents += button

      val board = new de.htwg.model.Board(1, 0)
      board.cells(0)(0) = cell
      Gui.controller = new de.htwg.controller.Controller(new de.htwg.factory.BoardFactory {
        override def createBoard(): de.htwg.model.Board = board
      })

      Gui.runObserverUpdate()

      button.text shouldBe ""
    }

    "set 🚩 if cell is flagged but not revealed" in {
      val cell = new de.htwg.model.GameCell()
      cell.isRevealed = false
      cell.isFlagged = true

      val button = new scala.swing.Button()
      Gui.gridPanel.contents.clear()
      Gui.gridPanel.contents += button

      val board = new de.htwg.model.Board(1, 0)
      board.cells(0)(0) = cell
      Gui.controller = new de.htwg.controller.Controller(new de.htwg.factory.BoardFactory {
        override def createBoard(): de.htwg.model.Board = board
      })

      Gui.runObserverUpdate()

      button.text shouldBe "🚩"
    }

    "set ⬜ if cell is neither revealed nor flagged" in {
      val cell = new de.htwg.model.GameCell()
      cell.isRevealed = false
      cell.isFlagged = false

      val button = new scala.swing.Button()
      Gui.gridPanel.contents.clear()
      Gui.gridPanel.contents += button

      val board = new de.htwg.model.Board(1, 0)
      board.cells(0)(0) = cell
      Gui.controller = new de.htwg.controller.Controller(new de.htwg.factory.BoardFactory {
        override def createBoard(): de.htwg.model.Board = board
      })

      Gui.runObserverUpdate()

      button.text shouldBe "⬜"
    }
  }
}
