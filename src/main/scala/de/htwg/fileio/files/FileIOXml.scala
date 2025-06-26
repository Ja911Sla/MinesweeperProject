package de.htwg.fileio

import de.htwg.model.BoardInterface
import de.htwg.model.boardBase.Board
import java.io._
import scala.xml._

class FileIOXml extends FileIOInterface {
  override def save(board: BoardInterface): Unit = {
    val pw = new PrintWriter(new File("board.xml"))
    pw.write(Board.toXml(board).toString())
    pw.close()
  }

  override def load(): BoardInterface = {
    val xml = XML.loadFile("board.xml")
    Board.fromXml(xml)
  }
}