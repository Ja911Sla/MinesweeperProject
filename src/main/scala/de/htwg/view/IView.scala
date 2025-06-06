package de.htwg.view

import scala.swing.*
import scala.swing.event.{MouseClicked, ButtonClicked}
import javax.swing.SwingUtilities
import de.htwg.controller._
import de.htwg.utility._
import de.htwg.factory.*

trait GuiInterface {
  def initialize(controller: ControllerInterface): Unit
  def attachController(controller: ControllerInterface): Unit
  def startGame(factory: BoardFactoryInterface): Unit
  def runObserverUpdate(): Unit
}

trait TuiInterface extends Observer {
  def start(resetBoard: Boolean = true): String
  def chooseDifficulty(): Unit
  def processInputLine(input: String): Boolean
  def requestQuit(): Unit
}

