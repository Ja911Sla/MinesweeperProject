error id: gameField.
file:///C:/HTWG_Module/Minesweeper_Project/MinesweeperProject/src/test/scala/de/htwg/PrintSpec.scala
empty definition using pc, found symbol in pc: gameField.
semanticdb not found
empty definition using fallback
non-local guesses:
	 -org/scalatest/matchers/should/Matchers.newPrint.gameField.
	 -org/scalatest/matchers/should/Matchers.newPrint.gameField#
	 -org/scalatest/matchers/should/Matchers.newPrint.gameField().
	 -de/htwg/newPrint/gameField.
	 -de/htwg/newPrint/gameField#
	 -de/htwg/newPrint/gameField().
	 -newPrint/gameField.
	 -newPrint/gameField#
	 -newPrint/gameField().
	 -scala/Predef.newPrint.gameField.
	 -scala/Predef.newPrint.gameField#
	 -scala/Predef.newPrint.gameField().
offset: 372
uri: file:///C:/HTWG_Module/Minesweeper_Project/MinesweeperProject/src/test/scala/de/htwg/PrintSpec.scala
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
      newPrint.game@@Field() should include("Congratulations, you won!")
    }
    "have a playing field that has a top bar like this: _________________________________________" in {
      val newPrint = Print()
      newPrint.gameField() should include("_________________________________________")
    }
    "have a playing field that has a bottom bar like this: |_______________________________________|" in {
      val newPrint = Print()
      newPrint.gameField() should include("|_______________________________________|")
    }
    "include smiley and flag emojis in game field" in {
      newPrint.gameField() should include("\uD83D\uDEA9") // won emoji
      newPrint.gameField() should include("\uD83D\uDEA9") // flag emoji
      newPrint.gameField() should include("\uD83D\uDE03") // smiley start
    }
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: gameField.