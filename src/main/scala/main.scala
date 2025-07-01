package de.htwg

import de.htwg.controller.ControllerInterface
import de.htwg.controller.controllerBase.Controller
import de.htwg.view.Tui
import de.htwg.view.Gui
import scala.concurrent.Future
import de.htwg.controller.controllerBase.given_ControllerInterface
import scala.concurrent.ExecutionContext.Implicits.global
import de.htwg.controller.factory.BoardFactory
import de.htwg.fileio._


@main def runMain(): Unit = {
  // Wähle die gewünschte FileIO-Implementierung

  //val fileIO: FileIOInterface = new FileIOJson()
  val fileIO: FileIOInterface = new FileIOXml()
  // Erzeuge den Controller mit FileIO über Dependency Injection
  val controller: ControllerInterface = new Controller(BoardFactory.getInstance, fileIO)
  // Stellt den Controller für Klassen mit `using` bereit (z. B. TUI)
  given ControllerInterface = controller
  // Initialisiere GUI zuerst
  Gui.initialize(controller)

  // Starte die TUI in einem separaten Thread
  val tui = new Tui()
  val tuiThread = new Thread(new Runnable {
    override def run(): Unit = {
      Thread.sleep(100) // Kurze Verzögerung, um sicherzustellen, dass die GUI initialisiert ist
      tui.start(resetBoard = false)
    }
  })
  tuiThread.setDaemon(true) // Programm beendet sich, wenn GUI geschlossen wird
  tuiThread.start()

  // starte die GUI
  Gui.main(Array.empty)
}