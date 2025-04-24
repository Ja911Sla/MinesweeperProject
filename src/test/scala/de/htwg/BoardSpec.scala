import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg._

class BoardSpec extends AnyWordSpec {
  "A Board" should {
//    "be able to reset" in {
//    val board = Board()
//      board.reset() should be
//    }
    "be able to place bombs" in {
      val board = Board()
      val amountMinesBefore = for {
        x <- 0 until board.size
        y <- 0 until board.size
        if (board.cells(x)(y)).isMine
      } yield (x,y)

      board.placeMines()

      val amountMinesAfter = for {
        x <- 0 until board.size
        y <- 0 until board.size
        if (board.cells(x)(y)).isMine
      } yield (x,y)
      amountMinesBefore.size should be (0)
      amountMinesAfter.size should be (10)
    }
  }
}
