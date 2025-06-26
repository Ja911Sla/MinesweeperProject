package de.htwg.fileio

import de.htwg.model.BoardInterface
import de.htwg.model.boardBase.Board
import java.io._
import scala.io.Source
import play.api.libs.json._
import de.htwg.fileio._

class FileIOJson extends FileIOInterface {
  override def save(board: BoardInterface): Unit = {
    val pw = new PrintWriter(new File("board.json"))
    pw.write(Json.prettyPrint(Board.toJson(board)))
    pw.close()
  }

  override def load(): BoardInterface = {
    val source = Source.fromFile("board.json").getLines().mkString
    Board.fromJson(Json.parse(source))
  }
}