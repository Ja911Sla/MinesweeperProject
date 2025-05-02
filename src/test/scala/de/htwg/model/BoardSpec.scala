package de.htwg.model

import de.htwg.model.Board
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import scala.Console

import java.io.*


class BoardSpec extends AnyWordSpec {
  "A Board" should {
    "be able to reset" in {       //Reset
      val board = Board()
      var placesMinesBefore = 0
      var x = 0
      while (x < board.size) {
        var y = 0
        while (y < board.size) {
          if (board.cells(x)(y).isMine) placesMinesBefore += 1
          y += 1
        }
        x += 1
      }
      board.reset()
      var placesMinesAfter = 0
      x = 0
      while (x < board.size) {
        var y = 0
        while (y < board.size) {
          if (board.cells(x)(y).isMine) placesMinesAfter += 1
          y += 1
        }
        x += 1
      }
      placesMinesBefore should not equal placesMinesAfter
    }
  }

  "be able to place bombs" in {     //Place
    val board = Board()
    var amountMinesBefore = 0
    var x = 0
    while (x < board.size) {
      var y = 0
      while (y < board.size) {
        if (board.cells(x)(y).isMine) amountMinesBefore += 1
        y += 1
      }
      x += 1
    }
    board.placeMines()
    var amountMinesAfter = 0
    x = 0
    while (x < board.size) {
      var y = 0
      while (y < board.size) {
        if (board.cells(x)(y).isMine) amountMinesAfter += 1
        y += 1
      }
      x += 1
    }
    amountMinesBefore should be(0)
    amountMinesAfter should be(board.mineCount)
    }

  "be able to reveal adjacents of its Cells" in {       //RevealAdjacent
    val board = Board()

    var x = 0
    while (x < board.size) {
      var y = 0
      while (y < board.size) {
        board.cells(x)(y).isMine = false
        y += 1
      }
      x += 1
    }

    board.cells(2)(2).isRevealed = false
    board.cells(3)(2).isRevealed = false
    board.cells(2)(1).isRevealed = false
    board.cells(3)(1).isRevealed = false
    board.cells(4)(2).isRevealed = false
    board.cells(4)(1).isRevealed = false

    board.cells(2)(2).isMine = false
    board.cells(3)(2).isMine = false
    board.cells(2)(1).isMine = false
    board.cells(3)(1).isMine = false
    board.cells(4)(2).isMine = false
    board.cells(4)(1).isMine = false
    board.cells(1)(2).isMine = true
    board.cells(3)(3).isMine = true
    board.cells(5)(2).isMine = true

    board.revealAdjacent(3, 1)

    board.cells(2)(2).isRevealed should be (true)
    board.cells(3)(2).isRevealed should be (true)
    board.cells(2)(1).isRevealed should be (true)
    board.cells(3)(1).isRevealed should be (true)
    board.cells(4)(2).isRevealed should be (true)
    board.cells(4)(1).isRevealed should be (true)
  }


  "be able to toggle flags on not revealed fields" in {    //Toggle
    val board = Board()
    board.cells(2)(2).isFlagged = false
    board.toggleFlag(2, 2)
    board.cells(2)(2).isFlagged should be (true)
    board.toggleFlag(2, 2)
    board.cells(2)(2).isFlagged should be (false)
  }
  "not toggle flags on revealed fields" in {
    val board = Board()
    board.cells(2)(2).isRevealed = true
    board.cells(2)(2).isFlagged = false
    board.toggleFlag(2, 2)
    board.cells(2)(2).isFlagged should be (false)
  }

  "be able to know if inbounds or not" in {     // Inbound
    val board = Board()
    board.inBounds(4, 3) should be (true)
    board.inBounds(11, 14) should be (false)
  }

  "reveal a mine-free cell and mark it as revealed" in {    // Reveal
    val board = Board()
    board.cells(3)(3).isMine = false
    val revealed = board.reveal(3, 3)
    revealed should be (true)
    board.cells(3)(3).isRevealed should be (true)
  }

  "reveal a mine and return false" in {
    val board = Board()
    board.cells(4)(4).isMine = true
    val revealed = board.reveal(4, 4)
    revealed should be (false)
  }

  "return true on checkWin only if all mines are flagged" in {      // Check Win
    val board = Board(mineCount = 3)

    board.cells(0)(0).isMine = true
    board.cells(1)(1).isMine = true
    board.cells(2)(2).isMine = true
    board.toggleFlag(0, 0)
    board.toggleFlag(1, 1)
    board.toggleFlag(2, 2)
    board.checkWin() should be (true)

    board.toggleFlag(0, 0)
    board.checkWin() should be (false)
  }

  "be able to count adjacent mines" in {      // CountAdjacentMines
    val board = Board()

    board.cells(3)(2).isMine = true
    board.cells(3)(4).isMine = true
    board.cells(2)(3).isMine = true
    board.cells(2)(4).isMine = true

    board.countAdjacentMines(3, 3) should be (4)
  }

  "be able to display itself" in {          // Display

    val board = Board(2, 2)
    board.cells(0)(0).isMine = true
    board.cells(0)(0).isRevealed = false
    board.cells(0)(1).isRevealed = false
    board.cells(1)(1).isMine = true
    board.cells(1)(1).isRevealed = false
    board.cells(1)(0).isRevealed = false

    board.reveal(0, 0)


    val out = new ByteArrayOutputStream()
    scala.Console.withOut(new PrintStream(out)) {
      board.display(true)
    }

    val output = out.toString()

    output should include("A")
    output should include("1 2")
    output should include("\uD83D\uDCA3") // Bombe
  }
  "display correctly with revealAll = false for flagged, revealed, and hidden cells" in {
    val board = Board(2, 0)

    board.cells(0)(0).isFlagged = true

    board.cells(0)(1).isRevealed = true
    board.cells(0)(1).mineCount = 1

    board.cells(1)(0).isRevealed = false

    // Zelle B2:
    board.cells(1)(1).isRevealed = true
    board.cells(1)(1).mineCount = 0

    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      board.display(false) // NICHT revealAll
    }

    val output = out.toString

    output should include("🚩")
    output should include("1️⃣")
    output should include("⬛")
    output should include("⬜")
  }
  "reveal returns true on flagged or revealed cells" in {
    val board = Board()
    board.cells(0)(0).isFlagged = true
    board.reveal(0, 0) shouldBe true

    board.cells(1)(1).isRevealed = true
    board.reveal(1, 1) shouldBe true
  }
  "return false if more flags than mines are set, even if all mines are flagged" in {
    val board = Board(mineCount = 2)

    board.cells(0)(0).isMine = true
    board.cells(1)(1).isMine = true
    board.cells(2)(2).isMine = false

    board.toggleFlag(0, 0)
    board.toggleFlag(1, 1)
    board.toggleFlag(2, 2)

    board.checkWin() should be(false)
  }

  "return false if not all mines are flagged, but totalFlags == mineCount" in {
    val board = Board(mineCount = 2)

    board.cells(0)(0).isMine = true
    board.cells(1)(1).isMine = true
    board.cells(2)(2).isMine = false

    board.toggleFlag(0, 0)
    board.toggleFlag(2, 2)

    board.checkWin() should be(false)
  }
}




