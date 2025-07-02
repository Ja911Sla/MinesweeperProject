package de.htwg.fileio.files

import de.htwg.fileio.FileIOJson
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.model.boardBase.Board

import java.io.File

class FileIOJsonSpec extends AnyWordSpec with Matchers {

  "A FileIOJson" should {

    "save a board to board.json and read it back correctly" in {
      val file = new File("board.json")
      if (file.exists()) file.delete()

      val board = Board(3, 2) // Beispielbrett: 3x3 mit 2 Minen
      val fileIO = new FileIOJson

      fileIO.save(board)
      file.exists() shouldBe true

      val loadedBoard = fileIO.load()
      loadedBoard.size shouldBe board.size
      loadedBoard.remainingFlags() shouldBe board.remainingFlags()
    }

    "handle multiple save/load cycles without error" in {
      val fileIO = new FileIOJson
      val board = Board(5, 3)

      fileIO.save(board)
      val board2 = fileIO.load()
      fileIO.save(board2)
      val board3 = fileIO.load()

      board3.size shouldBe board.size
    }
  }
}

