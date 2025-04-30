import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import de.htwg.*
import de.htwg.model.Field

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
    "return correct bomb status for true and false" in {
      val bombField = Field(1, 1, true)
      val safeField = Field(1, 1, false)

      bombField.hasBomb() should be(true)
      safeField.hasBomb() should be(false)
    }
    "correctly report information when bomb is true" in {
      val field = Field(2, 3, true)
      field.informationField() should be("X: 2\nY: 3\nBomb: true")
    }
    "compute 0 for fields with 0 width or height" in {
      val zeroX = Field(0, 5)
      val zeroY = Field(3, 0)
      val zeroXY = Field(0, 0)

      zeroX.computeAmountOfFields() should be(0)
      zeroY.computeAmountOfFields() should be(0)
      zeroXY.computeAmountOfFields() should be(0)
    }
  }
}
