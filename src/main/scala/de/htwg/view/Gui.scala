package de.htwg.view

import scala.swing.*
import scala.swing.event.{MouseClicked, ButtonClicked}
import javax.swing.SwingUtilities
import de.htwg.controller.Controller
import de.htwg.utility.Observer
import de.htwg.factory.*

object Gui extends SimpleSwingApplication {

  var controller: Controller = _
  var tui: Tui = _
  var tuiThread: Thread = _

  var startGameHandler: BoardFactory => Unit = startGame _      // Für Tests

  // Main panel to hold everything
  val mainPanel = new BorderPanel

  // Difficulty selection panel

  val difficultyPanel: BoxPanel = new BoxPanel(Orientation.Vertical) {

    contents += new Label("Schwierigkeit wählen:")
    val easyButton = new Button("Leicht (6x6, 5 Minen)")
    val mediumButton = new Button("Mittel (9x9, 15 Minen)")
    val hardButton = new Button("Schwer (12x12, 35 Minen)")

    contents ++= Seq(easyButton, mediumButton, hardButton)

    listenTo(easyButton, mediumButton, hardButton)

    reactions += {
      case ButtonClicked(`easyButton`) =>
        startGameHandler(EasyBoardFactory)        // startGame(EasyBoardFactory) ursprünglich - angepasst für Tests
        controller.createNewBoard(EasyBoardFactory)
      case ButtonClicked(`mediumButton`) =>
        startGameHandler(MediumBoardFactory)
        controller.createNewBoard(MediumBoardFactory)
      case ButtonClicked(`hardButton`) =>
        startGameHandler(HardBoardFactory)
        controller.createNewBoard(HardBoardFactory)
    }
  }

  // Grid panel for the game board
  val gridPanel = new GridPanel(1, 1) // darf nicht 0,0 sein

  // Lazy new game button (so it can reference itself)
  lazy val newGameButton: Button = new Button(Action("Neues Spiel") {
    mainPanel.layout -= gridPanel
    mainPanel.layout -= newGameButton
    mainPanel.layout(difficultyPanel) = BorderPanel.Position.Center
    mainPanel.peer.revalidate()
    mainPanel.peer.repaint()
  })

  def top: Frame = new MainFrame {
    title = "Minesweeper"
    preferredSize = new Dimension(500, 500)

    contents = mainPanel

    // On close, make sure to stop TUI thread
    override def closeOperation(): Unit = {
      println("Programm wird beendet.")
      if (tui != null) tui.requestQuit()
      sys.exit(0)
    }

    // Initially show difficulty panel
    mainPanel.layout(difficultyPanel) = BorderPanel.Position.Center
  }

  def attachController(sharedController: Controller): Unit = {
    this.controller = sharedController
    controller.add(GuiObserver)
  }
  def startGame(factory: BoardFactory): Unit = {
    controller.createNewBoard(factory)


    if (tui == null) {
      tui = new Tui(controller)
      tui.guiQuitCallback = () => {
        println("TUI hat Quit-Signal gegeben. Beende GUI...")
        top.close()
      }
      tuiThread = new Thread(new Runnable {
        override def run(): Unit = tui.start()
      })
      tuiThread.start()
    }

    val size = controller.getBoard.size
    gridPanel.rows = size
    gridPanel.columns = size
    gridPanel.contents.clear()

    for (row <- 0 until size; col <- 0 until size) {
      val cellButton = new Button {
        text = "⬜"
        listenTo(mouse.clicks)
        reactions += {
          case e: MouseClicked if e.peer.getButton == java.awt.event.MouseEvent.BUTTON1 =>
            if (controller.getBoard.cells(row)(col).isMine) {
              val isMineHit = controller.revealCell(row, col)
              handleGameOver()
            } else {
              controller.revealCell(row, col)
            }

          case e: MouseClicked if e.peer.getButton == java.awt.event.MouseEvent.BUTTON3 =>
            controller.flagCell(row, col)
        }
      }
      //listenTo(cellButton.mouse.clicks)
      gridPanel.contents += cellButton
    }

    mainPanel.layout(gridPanel) = BorderPanel.Position.Center
    mainPanel.layout(newGameButton) = BorderPanel.Position.South

    mainPanel.peer.revalidate()
    mainPanel.peer.repaint()
  }

  private def handleGameOver(): Unit = {
    // Deaktiviere alle Buttons
    for (button <- gridPanel.contents)
      button.enabled = false

    // Zeige alle Zellen (force update)
    for (row <- 0 until controller.getBoard.size;
         col <- 0 until controller.getBoard.size) {
      controller.getBoard.cells(row)(col).isRevealed = true
    }

    // GUI aktualisieren
    GuiObserver.update

    // Zeige Dialog
    Dialog.showMessage(mainPanel, "💥 Game Over – Du hast eine Mine erwischt!", "Verloren", Dialog.Message.Error)
  }

  // Observer to update the GUI when controller notifies
  private object GuiObserver extends Observer {
    override def update: String = {
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
  def runObserverUpdate(): Unit = GuiObserver.update // öffentliche methode für die tests
}
