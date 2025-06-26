package de.htwg.model.boardBase

import de.htwg.model.BoardInterface
import play.api.libs.json._
import scala.xml._

case class Board(val size: Int = 9, val mineCount: Int = 10) extends BoardInterface {
  override val cells: Array[Array[GameCell]] = Array.fill(size, size)(GameCell()) //cells füllt das 2D Array mit GameCell Objekten
  private val directions = List((-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)) // liste von alle möglichen Nachbarn

  private def dynamicHeader: String = {
    (1 to size).map(_.toString).zipWithIndex.map { case (n, i) =>
      if ((i + 1) % 5 == 0 && i + 1 != size) n + "  "
      else n + " "
    }.mkString.trim
  }

  def reset(): String = {
    var x = 0
    while (x < size) {
      var y = 0
      while (y < size) {
        cells(x)(y).isMine = false
        cells(x)(y).isRevealed = false
        cells(x)(y).isFlagged = false
        y += 1
      }
      x += 1
    }
    val minesPlaced = placeMines()
    s"Board reset. Mines placed: $minesPlaced"
  }

  def placeMines(): Int = {
    val random = scala.util.Random
    var amountMines = mineCount
    var placed = 0
    while (amountMines > 0) {
      val x = random.nextInt(size)
      val y = random.nextInt(size)
      if (!cells(x)(y).isMine) {
        cells(x)(y).isMine = true
        amountMines -= 1
        placed += 1
      }
    }
    placed
  }

  def reveal(row: Int, col: Int): Boolean = {
    if (cells(row)(col).isFlagged || cells(row)(col).isRevealed) return true // Zelle bereits aufgedeckt oder markiert
    if (cells(row)(col).isMine) return false // Mine aufgedeckt, Spiel verloren
    val count = countAdjacentMines(row, col) // Zähle die Minen in den Nachbarzellen
    cells(row)(col).mineCount = count // Setze die Anzahl der benachbarten Minen in der Zelle
    cells(row)(col).isRevealed = true // Zelle aufdecken
    if (count == 0) revealAdjacent(row, col) // Wenn keine Minen in den Nachbarzellen, decke angrenzende Zellen auf
    true
  }

  def inBounds(r: Int, c: Int): Boolean = { // nimmt die Zeile r und Spalte c als Parameter im 2D Array
    r >= 0 && r < size && c >= 0 && c < size // Überprüfe, ob die Zelle innerhalb der Grenzen des Spielfelds liegt
  }

  def countAdjacentMines(row: Int, col: Int): Int = { // zählt die Anzahl der Minen in den Nachbarzellen
    directions.count { case (dr, dc) => // .count Iteriert über alle Nachbarn die die isMine Bedingung erfüllt. dr= direchtion row, dc= direction column 
      val nr = row + dr // addiert man die Zeilenverschiebung zu der aktuellen Zeile hat man die Koordinate der Nachbarzelle
      val nc = col + dc // genau das gleiche macht man hier für die Spalte
      inBounds(nr, nc) && cells(nr)(nc).isMine // Prüft die Grenzen und ob die Nachbarzelle eine Mine hat
    }
  }

  def revealAdjacent(row: Int, col: Int): Unit = {
    // Iteriere über alle Nachbarn indem du die positionen berechneste wie oben nr = row + dr 
    // Überprüfe ob Nachbarzelle im Spielfeldgrenze liegt (inBounds) und noch nicht markiert ist (isFlagged)
    // erst nach beiden Bedingungen wird die Zelle aufgedeckt (reveal)
    directions.foreach { case (dr, dc) =>
      val nr = row + dr
      val nc = col + dc
      if (inBounds(nr, nc) && !(cells(nr)(nc).isMine) && !(cells(nr)(nc).isFlagged) && !(cells(nr)(nc).isRevealed)) {
        reveal(nr, nc)
      }
    }
  }


  def toggleFlag(row: Int, col: Int): Unit = {
    // Wenn die Zelle nicht aufgedeckt ist, dann toggle die Flagge (isFlagged) der Zelle
    // Wenn die Zelle aufgedeckt ist, dann mache nichts
    // toggle bedeutet, dass wenn die Flagge gesetzt ist, sie entfernt wird und umgekehrt
    if (!cells(row)(col).isRevealed) {
      cells(row)(col).isFlagged = !cells(row)(col).isFlagged // Setzt Flag auf das Gegenteil was es gerade ist.
    }
  }

  def checkWin(): Boolean = {
    val allSafeRevealed = cells.flatten.forall(c => c.isRevealed || c.isMine) // prüfe alle minen und sichere Zellen
    val flaggedMines = cells.flatten.count(c => c.isFlagged && c.isMine) // prüfe ob alle minen markiert sind
    val totalFlags = cells.flatten.count(_.isFlagged) // prüfe ob alle Zellen markiert sind
    allSafeRevealed || (flaggedMines == mineCount && totalFlags == mineCount) // prüfe ob Anzahl markierten Zellen = Anzahl der Minen ist
  }

  def display(revealAll: Boolean = false): String = {
    val sb = new StringBuilder
    sb.append(bombCountDisplayString() + "\n")
    sb.append("   " + dynamicHeader + "\n") // dynamic header depending on size of the Board

    for (r <- 0 until size) {
      sb.append((r + 'A').toChar + " ") // Row label (A, B, ...)
      for (c <- 0 until size) {
        val cell = cells(r)(c)
        val mark =
          if (revealAll && cell.isMine) "\uD83D\uDCA3"
          else if (cell.isFlagged) "\uD83D\uDEA9"
          else if (cell.isRevealed) {
            cell.mineCount match {
              case 0 => "⬛"
              case 1 => "1\uFE0F⃣"
              case 2 => "2\uFE0F⃣"
              case 3 => "3\uFE0F⃣"
              case 4 => "4\uFE0F⃣"
              case 5 => "5\uFE0F⃣"
              case n => s"$n "
            }
          } else "⬜"
        sb.append(mark)
      }
      sb.append("\n")
    }
    sb.toString() // <- THIS is what's returned to tests
  }

  def bombCountDisplayString(): String = {
    val flagged = cells.flatten.count(_.isFlagged)
    s"Bomb amount: ${mineCount - flagged}"
  }

  override def copyBoard(): BoardInterface = { // copy board neu implementiert für die undo und redo funktionen
    val newBoard = new Board(size, mineCount)
    for (r <- 0 until size; c <- 0 until size) {
      val orig = this.cells(r)(c)
      val copy = newBoard.cells(r)(c)
      copy.isMine = orig.isMine
      copy.isRevealed = orig.isRevealed
      copy.isFlagged = orig.isFlagged
      copy.mineCount = orig.mineCount
    }
    newBoard
  }

  def remainingFlags(): Int = {
    mineCount - cells.flatten.count(_.isFlagged)
  }
}

object Board {
  import play.api.libs.json._
  import scala.xml._

  // JSON-Format aus vorheriger Antwort
  implicit val boardFormat: Format[Board] = new Format[Board] {
    override def reads(json: JsValue): JsResult[Board] = {
      val size = (json \ "size").as[Int]
      val mineCount = (json \ "mineCount").as[Int]
      val board = new Board(size, mineCount)
      val cellsJson = (json \ "cells").as[JsArray]

      for (cellJson <- cellsJson.value) {
        val row = (cellJson \ "row").as[Int]
        val col = (cellJson \ "col").as[Int]
        val cell = board.cells(row)(col)
        cell.isMine = (cellJson \ "isMine").as[Boolean]
        cell.isRevealed = (cellJson \ "isRevealed").as[Boolean]
        cell.isFlagged = (cellJson \ "isFlagged").as[Boolean]
        cell.mineCount = (cellJson \ "mineCount").as[Int]
      }

      JsSuccess(board)
    }

    override def writes(board: Board): JsValue = Json.obj(
      "size" -> board.size,
      "mineCount" -> board.mineCount,
      "cells" -> JsArray(
        for {
          row <- board.cells.indices
          col <- board.cells(row).indices
        } yield Json.obj(
          "row" -> row,
          "col" -> col,
          "isMine" -> board.cells(row)(col).isMine,
          "isRevealed" -> board.cells(row)(col).isRevealed,
          "isFlagged" -> board.cells(row)(col).isFlagged,
          "mineCount" -> board.cells(row)(col).mineCount
        )
      )
    )
  }

  def toJson(board: BoardInterface): JsValue =
    Json.toJson(board.asInstanceOf[Board])

  def fromJson(json: JsValue): Board =
    json.as[Board]

  def toXml(board: BoardInterface): Elem = {
    <board size={board.size.toString} mineCount={board.mineCount.toString}>
      {
      for {
        row <- board.cells.indices
        col <- board.cells(row).indices
      } yield {
        val cell = board.cells(row)(col)
          <cell
          row={row.toString}
          col={col.toString}
          isMine={cell.isMine.toString}
          isRevealed={cell.isRevealed.toString}
          isFlagged={cell.isFlagged.toString}
          mineCount={cell.mineCount.toString}
          />
      }
      }
    </board>
  }

  def fromXml(xml: Elem): Board = {
    val size = (xml \ "@size").text.toInt
    val mineCount = (xml \ "@mineCount").text.toInt
    val board = new Board(size, mineCount)

    for (cell <- xml \\ "cell") {
      val row = (cell \ "@row").text.toInt
      val col = (cell \ "@col").text.toInt
      val c = board.cells(row)(col)
      c.isMine = (cell \ "@isMine").text.toBoolean
      c.isRevealed = (cell \ "@isRevealed").text.toBoolean
      c.isFlagged = (cell \ "@isFlagged").text.toBoolean
      c.mineCount = (cell \ "@mineCount").text.toInt
    }

    board
  }
}
