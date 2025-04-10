import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg._

class MinesweeperSpec extends AnyWordSpec {
  "A Minesweeper game" should {
    "compute the amount of fields correctly" in {
      val newField = Field(4, 5)
      newField.computeAmountOfFields() should be(20)
    }
    "know if a field has a bomb" in {
      val newField = Field(4, 5, true)
      newField.hasBomb() should be (true)
    }
    "know the status of each of its fields" in {
      val newField = Field(4, 5, false)
      newField.informationField() should be (s"X: 4\nY: 5\nBomb: false")
    }
  }
}
