package de.htwg.model

import de.htwg.*
import de.htwg.model.GameCell
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class GameCellSpec extends AnyWordSpec {
  "A Game cell" should {
    "contain" in {
      val cell = GameCell(true, false, false, 0)
      cell.isMine should be (true)
      cell.isRevealed should be (false)
      cell.isFlagged should be (false)
      cell.mineCount should be (0)
      
      cell.isMine = false
      cell.isRevealed = true
      cell.isFlagged = true
      cell.mineCount = 2

      cell.isMine should be(false)
      cell.isRevealed should be(true)
      cell.isFlagged should be(true)
      cell.mineCount should be(2)
    }
  }
}
