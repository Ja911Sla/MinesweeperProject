// filepath: c:\HTWG_Module\Minesweeper_Project\MinesweeperProject\src\main\scala\main.scala
import de.htwg.*
import de.htwg.view.Tui
import de.htwg.controller.Controller
import de.htwg.model.Board
import de.htwg.utility.Observer

@main
def main(): Unit = {
  // println("It works!!!!! hopefullybbbbbbbbbaaaaaaaa")
  // val myGame = Print()
  // print(myGame.gameField())
  // ab hier ausgabe von der TUI  

  val board = new Board()
  val controller = new Controller(board)
  val tui = new Tui(controller)
  tui.start()
}