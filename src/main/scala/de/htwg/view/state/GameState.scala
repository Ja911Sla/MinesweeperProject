package de.htwg.view.state

import de.htwg.controller.controllerBase.Controller
import de.htwg.view.Tui

trait GameState {
  def handleInput(input: String, tui: Tui): Boolean
}

case object IdleState extends GameState {

  override def handleInput(input: String, tui: Tui): Boolean = {
    println("Starting new game...")
    tui.chooseDifficulty()
    tui.controller.resetGame()
    tui.state = PlayingState
    true
  }
}

case object PlayingState extends GameState {
  override def handleInput(input: String, tui: Tui): Boolean = {
    tui.processInputLine(input)
  }
}


case object WonState extends GameState {
  override def handleInput(input: String, tui: Tui): Boolean = {
    println(tui.controller.displayBoardToString(true))
    println("Du hast gewonnen!")
    println("Spielzeit: " + tui.controller.getElapsedTime + " Sekunden.")
    true
  }
}



case object LostState extends GameState {
  override def handleInput(input: String, tui: Tui): Boolean = {
    println(tui.controller.displayBoardToString(true))
    println("BOOOM! Du hast verloren.")
    println("Spielzeit: " + tui.controller.getElapsedTime + " Sekunden.")
    println("""
        |Was möchtest du tun?
        |U - Undo
        |R - Restart
        |Q - Quit
        |""".stripMargin)
    val cmd = input.trim.toUpperCase
    cmd match {
      case "U" =>
        tui.controller.undo()
        tui.state = PlayingState
        true
      case "R" =>
        tui.state = RestartState
        tui.state.handleInput("R", tui)
      case "Q" =>
        tui.state = QuitState
        tui.state.handleInput("Q", tui)
      case _ =>
        println("Ungültige Eingabe. Optionen: U (Undo), R (Restart), Q (Quit)")
        true
    }
  }
}
case object QuitState extends GameState {
  override def handleInput(input: String, tui: Tui): Boolean = {
    println("Danke für's Spielen!")
    false
  }
}
case object RestartState extends GameState {
  override def handleInput(input: String, tui: Tui): Boolean = {
    println("Spiel zurückgesetzt.")
    tui.controller.resetGame()
    tui.state = PlayingState
    true
  }
}

case object MenuState extends GameState {
  override def handleInput(input: String, tui: Tui): Boolean = {
    println(
      """
        |Modus-Menü:
        |1 - Schwierigkeit ändern (Spiel wird neugestartet)
        |2 - Zurück zum Spiel
        |""".stripMargin
    )
    val choice = scala.io.StdIn.readLine().trim
    choice match {
      case "1" =>
        tui.chooseDifficulty()
        tui.controller.resetGame()
        println("Spiel mit neuer Schwierigkeit gestartet.")
        tui.state = PlayingState
        true
      case "2" =>
        println("Zurück zum Spiel.")
        tui.state = PlayingState
        true
      case _ =>
        println("Ungültige Eingabe. Zurück zum Spiel.")
        tui.state = PlayingState
        true
    }
  }
}