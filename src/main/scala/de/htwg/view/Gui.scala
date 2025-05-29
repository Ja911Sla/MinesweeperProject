package de.htwg.view

import scala.swing._
import scala.swing.event._
import javax.swing.SwingUtilities
import de.htwg.controller.Controller
import de.htwg.utility.Observer
import de.htwg.factory._
import de.htwg.view.Tui
import de.htwg.model.GameCell
import de.htwg.factory._
import scala.swing.event.MouseEvent


object Gui extends SimpleSwingApplication with Observer {

  var controller: Controller = _
  var tui: Tui = _
  var tuiThread: Thread = _

  def top: Frame = new MainFrame {
    title = "Minesweeper"
    preferredSize = new Dimension(500, 500)

    // Beim Schließen sicherstellen, dass alles sauber beendet wird
    override def closeOperation(): Unit = {
      println("Programm wird beendet.")
      if (tui != null) tui.requestQuit()  // Signal an TUI
      sys.exit(0)
    }

    // Schwierigkeit Auswahl Panel
    val difficultyPanel = new BoxPanel(Orientation.Vertical) {
      contents += new Label("Schwierigkeit wählen:")
      val easyButton = new Button("Leicht (6x6, 5 Minen)")
      val mediumButton = new Button("Mittel (9x9, 15 Minen)")
      val hardButton = new Button("Schwer (12x12, 35 Minen)")

      contents += easyButton
      contents += mediumButton
      contents += hardButton

      listenTo(easyButton, mediumButton, hardButton)

      reactions += {
        case ButtonClicked(`easyButton`) =>
          startGame(EasyBoardFactory)
        case ButtonClicked(`mediumButton`) =>
          startGame(MediumBoardFactory)
        case ButtonClicked(`hardButton`) =>
          startGame(HardBoardFactory)
      }
    }

    // Spiel-Grid Panel
    val gridPanel = new GridPanel(0, 0)

    contents = difficultyPanel

    def startGame(factory: BoardFactory): Unit = {
      controller = new Controller(factory)
      controller.add(this)

      if (tui == null) {
        tui = new Tui(controller)
        tui.guiQuitCallback = () => {
          println("TUI hat Quit-Signal gegeben. Beende GUI...")
          close()
        }
        tuiThread = new Thread(() => tui.start())
        tuiThread.start()
      }

      val size = controller.getBoard.size
      gridPanel.rows = size
      gridPanel.columns = size
      gridPanel.contents.clear()

      for (row <- 0 until size; col <- 0 until size) {
        val cellButton = new Button {
          text = "⬜"
          reactions += {
            case MouseClicked(_, _, MouseEvent.Left, _, _) =>
              controller.revealCell(row, col)
            case MouseClicked(_, _, MouseEvent.Right, _, _) =>
              controller.flagCell(row, col)
          }
        }
        listenTo(cellButton.mouse.clicks)
        gridPanel.contents += cellButton
      }

      contents = new BorderPanel {
        layout(gridPanel) = BorderPanel.Position.Center
        layout(new Button(Action("Neues Spiel") {
          contents = new BoxPanel(Orientation.Vertical) {
            contents += difficultyPanel
          }
        })) = BorderPanel.Position.South
      }

      revalidate()
      repaint()
    }

    override def update(): String = {
      SwingUtilities.invokeLater(() => {
        val board = controller.getBoard
        for ((button, idx) <- gridPanel.contents.zipWithIndex) {
          val row = idx / board.size
          val col = idx % board.size
          val cell = board.cells(row)(col)

          button.asInstanceOf[Button].text =
            if (cell.isRevealed) {
              if (cell.isMine) "💣"
              else if (cell.mineCount > 0) cell.mineCount.toString
              else ""
            } else if (cell.isFlagged) "🚩"
            else "⬜"
        }
      })
      ""
    }
  }
}
