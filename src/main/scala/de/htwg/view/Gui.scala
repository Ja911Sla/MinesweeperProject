package de.htwg.view

import de.htwg.controller.controllerBase.Controller

import scala.swing.*
import scala.swing.event.{ButtonClicked, MouseClicked}
import javax.swing.SwingUtilities
import de.htwg.utility.Observer
import de.htwg.controller.ControllerInterface
import de.htwg.controller.factory.{BoardFactory, EasyBoardFactory, HardBoardFactory, MediumBoardFactory}
import de.htwg.controller.controllerBase.given_ControllerInterface
object Gui extends SimpleSwingApplication {
  private var controllerOpt: Option[ControllerInterface] = None
  var tui: Tui = _
  var tuiThread: Thread = _

  def initialize(c: ControllerInterface): Unit = {
    controllerOpt = Some(c)
  }

  var startGameHandler: BoardFactory => Unit = startGame _ // Für Tests

  // Main panel to hold everything
  val mainPanel = new BorderPanel

  val flagCountLabel = new Label()


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
        startGameHandler(EasyBoardFactory) // startGame(EasyBoardFactory) ursprünglich - angepasst für Tests
        controllerOpt.get.createNewBoard(EasyBoardFactory)
      case ButtonClicked(`mediumButton`) =>
        startGameHandler(MediumBoardFactory)
        controllerOpt.get.createNewBoard(MediumBoardFactory)
      case ButtonClicked(`hardButton`) =>
        startGameHandler(HardBoardFactory)
        controllerOpt.get.createNewBoard(HardBoardFactory)
    }
  }

  // Grid panel for the game board
  val gridPanel = new GridPanel(1, 1) // darf nicht 0,0 sein

  // Lazy new game button (so it can reference itself)
  lazy val newGameButton: Button = new Button(Action("Neues Spiel") {
    mainPanel.layout -= gridPanel
    mainPanel.layout -= newGameButton
    mainPanel.layout(difficultyPanel) = BorderPanel.Position.North
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


  // Panel für Flag count
  val topControlPanel = new BorderPanel {
    layout(new FlowPanel(FlowPanel.Alignment.Center)(newGameButton)) = BorderPanel.Position.Center
    layout(flagCountLabel) = BorderPanel.Position.East
    flagCountLabel.border = Swing.EmptyBorder(0, 30, 0, 0) // oben, links, unten, rechts
    border = Swing.EmptyBorder(5, 65, 5, 10)
  }

  def attachController(c: ControllerInterface): Unit = {
    controllerOpt = Some(c)
    controllerOpt.foreach(_.add(GuiObserver))
  }

  def startGame(factory: BoardFactory): Unit = {
    require(controllerOpt.isDefined, "Controller wurde nicht initialisiert!")
    controllerOpt.foreach { controller =>
      controller.createNewBoard(factory)
      controller.setDifficultySet(true) // <<< wichtig für die TUI

      controller.add(GuiObserver)

      if (tui != null && tui.isRunning) {
        tui.runObserverUpdate()
      }

      if (tui == null) {
        tui = new Tui
        tui.guiQuitCallback = () => {
          println("TUI hat Quit-Signal gegeben. Beende GUI...")
          top.close()
        }
        tuiThread = new Thread(new Runnable {
          override def run(): Unit = {
            tui.start(resetBoard = false)
          }
        })

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
                  controller.revealCell(row, col)
                  handleGameOver()
                } else if (controller.getBoard.checkWin()) {
                  handleGameWon()
                } else {
                  controller.revealCell(row, col)
                }

              case e: MouseClicked if e.peer.getButton == java.awt.event.MouseEvent.BUTTON3 =>
                controller.flagCell(row, col)
            }
          }
          gridPanel.contents += cellButton
        }

        mainPanel.layout(topControlPanel) = BorderPanel.Position.North
        //mainPanel.layout(topPanel) = BorderPanel.Position.North
        mainPanel.layout(gridPanel) = BorderPanel.Position.Center
        //mainPanel.layout(newGameButton) = BorderPanel.Position.South:
        mainPanel.peer.revalidate()
        mainPanel.peer.repaint()
      }
      // Handle game over scenario


    }
  }

    var isGameOver = false

  def controller: ControllerInterface = controllerOpt.getOrElse(
    throw new IllegalStateException("Controller wurde nicht initialisiert!")
  )

    def handleGameOver(): Unit = {
      if (!isGameOver) {
        isGameOver = true
        // Deaktiviere alle Buttons
        for (button <- gridPanel.contents)
          button.enabled = false

        // Zeige alle Zellen
        for (row <- 0 until controller.getBoard.size;
             col <- 0 until controller.getBoard.size) {
          controller.getBoard.cells(row)(col).isRevealed = true
        }

        GuiObserver.update
        Dialog.showMessage(mainPanel, "💥 Game Over – Du hast eine Mine erwischt!", "Verloren", Dialog.Message.Error)
      }
    }

    def handleGameWon(): Unit = {
      for (button <- gridPanel.contents)
        button.enabled = false

      for (row <- 0 until controller.getBoard.size;
           col <- 0 until controller.getBoard.size) {
        controller.getBoard.cells(row)(col).isRevealed = true
      }

      GuiObserver.update
      Dialog.showMessage(mainPanel, "Gewonnen, Glückwunsch!", "Gewonnen", Dialog.Message.Info)
    }

    // Observer to update the GUI when controller notifies
    private val GuiObserver: Observer = new Observer {
      override def update: String = {
        SwingUtilities.invokeLater(() => {
          val board = controller.getBoard

          if (gridPanel.rows != board.size || gridPanel.columns != board.size) {
            gridPanel.rows = board.size
            gridPanel.columns = board.size
            gridPanel.contents.clear()

            for (row <- 0 until board.size; col <- 0 until board.size) {
              val cellButton = new Button {
                text = "⬜"
                listenTo(mouse.clicks)
                reactions += {
                  case e: MouseClicked if e.peer.getButton == java.awt.event.MouseEvent.BUTTON1 =>
                    if (!isGameOver) {
                      if (controller.getBoard.cells(row)(col).isMine) {
                        controller.revealCell(row, col)
                        handleGameOver()
                      } else if (controller.getBoard.checkWin()) {
                        handleGameWon()
                      } else {
                        controller.revealCell(row, col)
                      }
                    }
                  case e: MouseClicked if e.peer.getButton == java.awt.event.MouseEvent.BUTTON3 =>
                    controller.flagCell(row, col)
                }
              }
              gridPanel.contents += cellButton
            }

            mainPanel.layout(gridPanel) = BorderPanel.Position.Center
            mainPanel.layout(newGameButton) = BorderPanel.Position.South
            mainPanel.peer.revalidate()
            mainPanel.peer.repaint()
          }

          // Update alle Buttons
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
          flagCountLabel.text = s"🚩: ${controller.remainingFlags()}"
          mainPanel.peer.revalidate()
          mainPanel.peer.repaint()
        })
        ""
      }
    }

    def runObserverUpdate(): Unit = GuiObserver.update // öffentliche methode für die tests



}