
import de.htwg.*
import de.htwg.view.Tui
import de.htwg.controller.Controller
import de.htwg.model.Board
import de.htwg.utility.Observer
import de.htwg.factory._
import de.htwg.singleton.GameConfig
import de.htwg.strategy.GameModeStrategy

@main
def main(): Unit = {

  // ab hier ausgabe von der TUI
  // GameConfig.setMedium
  val controller = new Controller(ConfigBoardFactory)
  val tui = new Tui(controller)

  println(tui.start())  // <--- You need to actually do something with the result


}