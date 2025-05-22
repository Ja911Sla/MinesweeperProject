package de.htwg.model

import de.htwg.model.Board
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import scala.Console

import java.io.*


class BoardSpec extends AnyWordSpec {
  "A Board" should {
    "be able to reset" in { //Reset
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

  "be able to place bombs" in { //Place
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

  "be able to reveal adjacents of its Cells" in { //RevealAdjacent
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

    board.cells(2)(2).isRevealed should be(true)
    board.cells(3)(2).isRevealed should be(true)
    board.cells(2)(1).isRevealed should be(true)
    board.cells(3)(1).isRevealed should be(true)
    board.cells(4)(2).isRevealed should be(true)
    board.cells(4)(1).isRevealed should be(true)
  }


  "be able to toggle flags on not revealed fields" in { //Toggle
    val board = Board()
    board.cells(2)(2).isFlagged = false
    board.toggleFlag(2, 2)
    board.cells(2)(2).isFlagged should be(true)
    board.toggleFlag(2, 2)
    board.cells(2)(2).isFlagged should be(false)
  }
  "not toggle flags on revealed fields" in {
    val board = Board()
    board.cells(2)(2).isRevealed = true
    board.cells(2)(2).isFlagged = false
    board.toggleFlag(2, 2)
    board.cells(2)(2).isFlagged should be(false)
  }

  "be able to know if inbounds or not" in { // Inbound
    val board = Board()
    board.inBounds(4, 3) should be(true)
    board.inBounds(11, 14) should be(false)
  }

  "reveal a mine-free cell and mark it as revealed" in { // Reveal
    val board = Board()
    board.cells(3)(3).isMine = false
    val revealed = board.reveal(3, 3)
    revealed should be(true)
    board.cells(3)(3).isRevealed should be(true)
  }

  "reveal a mine and return false" in {
    val board = Board()
    board.cells(4)(4).isMine = true
    val revealed = board.reveal(4, 4)
    revealed should be(false)
  }

  "return true on checkWin only if all mines are flagged" in { // Check Win
    val board = Board(mineCount = 3)

    board.cells(0)(0).isMine = true
    board.cells(1)(1).isMine = true
    board.cells(2)(2).isMine = true
    board.toggleFlag(0, 0)
    board.toggleFlag(1, 1)
    board.toggleFlag(2, 2)
    board.checkWin() should be(true)

    board.toggleFlag(0, 0)
    board.checkWin() should be(false)
  }

  "be able to count adjacent mines" in { // CountAdjacentMines
    val board = Board()

    board.cells(3)(2).isMine = true
    board.cells(3)(4).isMine = true
    board.cells(2)(3).isMine = true
    board.cells(2)(4).isMine = true

    board.countAdjacentMines(3, 3) should be(4)
  }

  "be able to display itself" in {
    val board = Board(2, 2)
    board.cells(0)(0).isMine = true
    board.cells(0)(0).isRevealed = false
    board.cells(0)(1).isRevealed = false
    board.cells(1)(1).isMine = true
    board.cells(1)(1).isRevealed = false
    board.cells(1)(0).isRevealed = false

    board.reveal(0, 0)

    val output = board.display(true) // <--- Correct capture

    println("Captured Output for display(true):\n" + output)

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
    board.cells(1)(1).isRevealed = true
    board.cells(1)(1).mineCount = 0

    val output = board.display(false)

    println("Captured Output for display(false):\n" + output)

    output should include("\uD83D\uDEA9") // 🚩
    output should include("1\uFE0F⃣")     // 1️⃣
    output should include("⬛")            // ⬛
    output should include("⬜")            // ⬜
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
  "should display the amount of left bombs" in {
    val board = Board(mineCount = 2)
    board.cells(0)(0).isMine = true
    board.toggleFlag(0, 0)

    val output = board.bombCountDisplayString() // <- capture the returned String directly

    output should include("Bomb amount: 1") // <- assert on the returned String
  }
  "should display 6 when a revealed cell has 6 adjacent mines" in {
    val board = Board(3, 0)

    board.cells(0)(0).isMine = true
    board.cells(0)(1).isMine = true
    board.cells(0)(2).isMine = true
    board.cells(1)(0).isMine = true
    board.cells(1)(2).isMine = true
    board.cells(2)(1).isMine = true

    board.cells(1)(1).isRevealed = true
    board.cells(1)(1).mineCount = 6

    val output = board.display(false)  // <- capture the returned String

    output should include("6 ")         // <- assert on the content
  }
  "correctly display 2️⃣ for 2 adjacent mines" in {
    val board = Board(3, 0)
    board.cells(1)(1).isRevealed = true
    board.cells(1)(1).mineCount = 2

    val output = board.display(false)
    output should include("2️⃣")
  }

  "correctly display 3️⃣ for 3 adjacent mines" in {
    val board = Board(3, 0)
    board.cells(1)(1).isRevealed = true
    board.cells(1)(1).mineCount = 3

    val output = board.display(false)
    output should include("3️⃣")
  }

  "correctly display 4️⃣ for 4 adjacent mines" in {
    val board = Board(3, 0)
    board.cells(1)(1).isRevealed = true
    board.cells(1)(1).mineCount = 4

    val output = board.display(false)
    output should include("4️⃣")
  }

  "correctly display 5️⃣ for 5 adjacent mines" in {
    val board = Board(3, 0)
    board.cells(1)(1).isRevealed = true
    board.cells(1)(1).mineCount = 5

    val output = board.display(false)
    output should include("5️⃣")
  }

  "correctly display raw number for mineCount >= 6" in {
    val board = Board(3, 0)
    board.cells(1)(1).isRevealed = true
    board.cells(1)(1).mineCount = 6

    val output = board.display(false)
    output should include("6 ")
  }
  "support default display with no parameters" in {
    val board = Board(2, 0)
    val output = board.display() // <- default parameter used, no argument
    output should include("1 2")
    output should include("A")
  }

  "create a copy of itself" in {
    val board = Board(2, 0)
    board.cells(0)(0).isRevealed = false
    board.cells(0)(0).isMine = true
    board.cells(1)(0).isRevealed = true
    board.cells(1)(0).isMine = false
    board.cells(1)(1).isRevealed = false
    board.cells(1)(1).isFlagged = true
    board.cells(0)(1).isRevealed = false
    board.cells(0)(1).isMine = false

    val output = board.copyBoard()

    output.cells(0)(0).isRevealed should be (false)
    output.cells(0)(0).isMine should be (true)
    output.cells(1)(0).isRevealed should be (true)
    output.cells(1)(0).isMine should be (false)
    output.cells(1)(1).isRevealed should be (false)
    output.cells(1)(1).isFlagged should be(true)
    output.cells(0)(0).isRevealed should be (false)
    output.cells(0)(1).isMine should be (false)
  }

}




