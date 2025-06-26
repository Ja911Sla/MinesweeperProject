package de.htwg

import de.htwg.controller.ControllerInterface
import de.htwg.controller.controllerBase.Controller
import de.htwg.view.Tui
import de.htwg.view.Gui
import scala.concurrent.Future
import de.htwg.controller.controllerBase.given_ControllerInterface
import scala.concurrent.ExecutionContext.Implicits.global
import de.htwg.controller.factory.BoardFactory


@main def runMain(): Unit = {
  // Erzeuge den Controller und stelle ihn bereit via given
  val controller: ControllerInterface = new Controller(BoardFactory.getInstance)

  given ControllerInterface = controller

  // Starte die TUI in einem separaten Thread
  val tui = new Tui()
  val tuiThread = new Thread(new Runnable {
    override def run(): Unit = tui.start()
  })
  tuiThread.setDaemon(true) // damit das Programm bei GUI-Schließen auch beendet wird
  tuiThread.start()

  // Initialisiere und starte die GUI (läuft im EDT)
  Gui.initialize(controller)
  Gui.main(Array.empty)
}