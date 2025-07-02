package de.htwg.fileio.files


import de.htwg.fileio.FileIOXml
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.model.boardBase.Board

import java.io.File
import scala.xml.XML

class FileIOXmlSpec extends AnyWordSpec with Matchers {

  "A FileIOXml" should {

    "save a board to board.xml and load it back correctly" in {
      val file = new File("board.xml")
      if (file.exists()) file.delete()

      val board = Board(4, 2) // Beispielbrett: 4x4 mit 2 Minen
      val fileIO = new FileIOXml

      fileIO.save(board)
      file.exists() shouldBe true

      val loadedBoard = fileIO.load()
      loadedBoard.size shouldBe board.size
      loadedBoard.remainingFlags() shouldBe board.remainingFlags()
    }

    "save well-formed XML that can be parsed manually" in {
      val board = Board(5, 1)
      val fileIO = new FileIOXml
      fileIO.save(board)

      noException should be thrownBy {
        val xml = XML.loadFile("board.xml")
        xml.label should not be empty
      }
    }

    "handle multiple save/load cycles without data loss" in {
      val fileIO = new FileIOXml
      val board = Board(6, 4)

      fileIO.save(board)
      val loaded1 = fileIO.load()
      fileIO.save(loaded1)
      val loaded2 = fileIO.load()

      loaded2.size shouldBe board.size
      loaded2.remainingFlags() shouldBe board.remainingFlags()
    }
  }
}
