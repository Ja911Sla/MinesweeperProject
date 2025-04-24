import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg._

class BoardSpec extends AnyWordSpec {
  "A Board" should {
    "be able to reset" in {
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

  "be able to place bombs" in {

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
}


