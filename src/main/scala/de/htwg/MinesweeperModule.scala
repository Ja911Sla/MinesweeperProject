package de.htwg

import de.htwg.controller.ControllerInterface
import de.htwg.controller.controllerBase.Controller
import de.htwg.factory.BoardFactory
import de.htwg.view.{Gui, Tui}

object MinesweeperModule {
  // Hier binden wir Interfaces zu konkreten Instanzen
  val boardFactory: BoardFactory = BoardFactory.getInstance
  val controller: ControllerInterface = new Controller(boardFactory)
  val tui: Tui = new Tui(controller.asInstanceOf[de.htwg.controller.controllerBase.Controller])
  val gui: Gui.type = Gui

  def start(): Unit = {
    gui.attachController(controller.asInstanceOf[de.htwg.controller.controllerBase.Controller])
    scala.concurrent.Future {
      gui.main(Array.empty)
    }(scala.concurrent.ExecutionContext.global)

    println(tui.start())
  }
}
