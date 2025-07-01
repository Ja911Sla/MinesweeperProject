package de.htwg.view

import de.htwg.controller.controllerBase.Controller

import scala.swing.*
import scala.swing.event.{ButtonClicked, MouseClicked}
import javax.swing.SwingUtilities
import de.htwg.utility.Observer
import de.htwg.controller.ControllerInterface
import de.htwg.controller.factory.{BoardFactory, EasyBoardFactory, HardBoardFactory, MediumBoardFactory}
import de.htwg.controller.controllerBase.given_ControllerInterface
import de.htwg.controller.controllerBase.SetCommand
import de.htwg.controller.controllerBase.FlagCommand
object Gui extends SimpleSwingApplication {
  private var controllerOpt: Option[ControllerInterface] = None
  var tui: Tui = _
  var tuiThread: Thread = _
  private var guiObserverRegistered = false // Flag für Observer-Registrierung

  def initialize(c: ControllerInterface): Unit = {
    controllerOpt = Some(c)
    if (!guiObserverRegistered) {
      controllerOpt.foreach(_.add(GuiObserver))
      guiObserverRegistered = true
    }
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
      /* controllerOpt.get.createNewBoard(EasyBoardFactory)*/
      case ButtonClicked(`mediumButton`) =>
        startGameHandler(MediumBoardFactory)
      /*  controllerOpt.get.createNewBoard(MediumBoardFactory)*/
      case ButtonClicked(`hardButton`) =>
        startGameHandler(HardBoardFactory)
      /* controllerOpt.get.createNewBoard(HardBoardFactory)*/
    }
  }

  // Grid panel for the game board
  val gridPanel = new GridPanel(1, 1) // darf nicht 0,0 sein

  // Lazy new game button (so it can reference itself)
  lazy val newGameButton: Button = new Button(Action("Neues Spiel") {
    restartGame()
  })

  lazy val quitButton: Button = new Button(Action("Beenden") {
    if (Dialog.showConfirmation(mainPanel, "Möchtest du das Spiel wirklich beenden?", "Beenden") == Dialog.Result.Ok) {
      println("Spiel wird beendet.")
      if (tui != null) tui.requestQuit()
      top.close()
      sys.exit(0)
    }
  }) {
    font = new Font("Arial", java.awt.Font.PLAIN, 14)
    preferredSize = new Dimension(100, 30)
  }
  val topControlPanel = new BorderPanel {
    layout(new FlowPanel(FlowPanel.Alignment.Center)(newGameButton, quitButton)) = BorderPanel.Position.Center
    layout(flagCountLabel) = BorderPanel.Position.East
    flagCountLabel.border = Swing.EmptyBorder(0, 30, 0, 0)
    border = Swing.EmptyBorder(5, 65, 5, 10)
  }

  def top: Frame = new MainFrame {
    title = "Minesweeper"
    preferredSize = new Dimension(500, 500)
    contents = mainPanel

    override def closeOperation(): Unit = {
      println("Programm wird beendet.")
      if (tui != null) tui.requestQuit()
      sys.exit(0)
    }
    mainPanel.layout(difficultyPanel) = BorderPanel.Position.Center
  }

  def startGame(factory: BoardFactory): Unit = {
    require(controllerOpt.isDefined, "Controller wurde nicht initialisiert!")
    controllerOpt.foreach { controller =>
      // Nur createNewBoard aufrufen, wenn Schwierigkeit nicht gesetzt ist
      if (!controller.isDifficultySet) {
        controller.createNewBoard(factory)
        controller.setDifficultySet(true)
      }
      // TUI starten, falls noch nicht geschehen
      if (tui == null) {
        tui = new Tui
        tui.guiQuitCallback = () => {
          println("TUI hat Quit-Signal gegeben. Beende GUI...")
          if (top != null) top.close()
        }
        tuiThread = new Thread(new Runnable {
          override def run(): Unit = {
            tui.start(resetBoard = false)
          }
        })
        tuiThread.setDaemon(true)
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
              if (!controller.isGameOver) {
                val cmd = new SetCommand(row, col, controller)
                controller.doAndStore(cmd)
                if (controller.isGameOver) {
                  handleGameOver()
                } else if (controller.checkWin()) {
                  handleGameWon()
                }
              }

            case e: MouseClicked if e.peer.getButton == java.awt.event.MouseEvent.BUTTON3 =>
              if (!controller.isGameOver) {
                val cmd = new FlagCommand(row, col, controller)
                controller.doAndStore(cmd)
              }

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

      GuiObserver.update

    }
  }

  def controller: ControllerInterface = controllerOpt.getOrElse(
    throw new IllegalStateException("Controller wurde nicht initialisiert!")
  )

  def handleGameOver(): Unit = {
    if (!controller.isGameOver) return // Controller hat Vorrang
    for (button <- gridPanel.contents) button.enabled = false
    GuiObserver.update
    Dialog.showMessage(mainPanel, "💥 Game Over – Du hast eine Mine erwischt!", "Verloren", Dialog.Message.Error)
  }

  def handleGameWon(): Unit = {
    if (!controller.isWon) return // Controller hat Vorrang
    for (button <- gridPanel.contents) button.enabled = false
    GuiObserver.update
    Dialog.showMessage(mainPanel, "Gewonnen, Glückwunsch!", "Gewonnen", Dialog.Message.Info)
  }

   def restartGame(): Unit = {
    controller.setDifficultySet(false)
    controller.resetGame()
    mainPanel.layout.clear()
    mainPanel.layout(difficultyPanel) = BorderPanel.Position.Center
    mainPanel.peer.revalidate()
    mainPanel.peer.repaint()
    if (tui != null) tui.runObserverUpdate()
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
                  if (!controller.isGameOver && !controller.isWon) {
                    val cmd = new SetCommand(row, col, controller)
                    controller.doAndStore(cmd)
                    if (controller.isGameOver) {
                      handleGameOver()
                    } else if (controller.checkWin()) {
                      handleGameWon()
                    }
                  }
                case e: MouseClicked if e.peer.getButton == java.awt.event.MouseEvent.BUTTON3 =>
                  if (!controller.isGameOver && !controller.isWon) {
                    val cmd = new FlagCommand(row, col, controller)
                    controller.doAndStore(cmd)
                  }
              }
            }
            gridPanel.contents += cellButton
          }

          mainPanel.layout.clear()
          mainPanel.layout(topControlPanel) = BorderPanel.Position.North
          mainPanel.layout(gridPanel) = BorderPanel.Position.Center
          mainPanel.peer.revalidate()
          mainPanel.peer.repaint()
        }

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

  def runObserverUpdate(): Unit = GuiObserver.update
}



