
import de.htwg.*
import de.htwg.controller.controllerComponent.controllerBase.Controller
import de.htwg.view.Tui
import de.htwg.model.Board
import de.htwg.utility.Observer
import de.htwg.factory._
import de.htwg.singleton.GameConfig
import de.htwg.strategy.GameModeStrategy
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import de.htwg.view.Gui

// $COVERAGE-OFF$
@main
def main(): Unit = runMain()
// $COVERAGE-ON$
def runMain(): Unit = {
  val controller = new Controller(BoardFactory.getInstance)
  Gui.attachController(controller)
  val tui = new Tui(controller)

  Future { Gui.main(Array.empty) }
  println(tui.start())
}
