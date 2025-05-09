
import de.htwg.*
import de.htwg.view.Tui
import de.htwg.controller.Controller
import de.htwg.model.Board
import de.htwg.utility.Observer

@main
def main(): Unit = {

  // ab hier ausgabe von der TUI  
  println("Willkommen zu Minesweeper!")
  val board = new Board()
  val controller = new Controller(board)
  val tui = new Tui(controller)
  tui.start()
}