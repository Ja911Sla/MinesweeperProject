error id: `<none>`.
file:///C:/HTWG_Module/Minesweeper_Project/MinesweeperProject/src/test/scala/PrintSpec.scala
empty definition using pc, found symbol in pc: `<none>`.
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 474
uri: file:///C:/HTWG_Module/Minesweeper_Project/MinesweeperProject/src/test/scala/PrintSpec.scala
text:
```scala
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg._

class PrintSpec extends AnyWordSpec {
  "A Minesweeper game" should {
    "have a playing field that contains these sentences" in {
      val newPrint = Print()
      newPrint.gameField() should include("Time for Minesweeper!")
      newPrint.gameField() should include("Congratulations, you won!")
    }
    "have a playing field that has a top bar like @@this: _________________________________________" in {
      val newPrint = Print()
      newPrint.gameField() should include("_________________________________________")
    }
    "have a playing field that has a bottom bar like this: |_______________________________________|" in {
      val newPrint = Print()
      newPrint.gameField() should include("|_______________________________________|")
    }
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.