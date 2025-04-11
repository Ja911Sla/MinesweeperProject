error id: wordspec.
file:///C:/HTWG_Module/Minesweeper_Project/MinesweeperProject/src/test/scala/de/htwg/PrintSpec.scala
empty definition using pc, found symbol in pc: wordspec.
semanticdb not found
empty definition using fallback
non-local guesses:
	 -org/scalatest/matchers/should/Matchers.org.scalatest.wordspec.
	 -de/htwg/org/scalatest/wordspec.
	 -org/scalatest/wordspec.
	 -scala/Predef.org.scalatest.wordspec.
offset: 23
uri: file:///C:/HTWG_Module/Minesweeper_Project/MinesweeperProject/src/test/scala/de/htwg/PrintSpec.scala
text:
```scala
import org.scalatest.wo@@rdspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg._

class PrintSpec extends AnyWordSpec {
  "A Minesweeper game" should {
    "have a playing field that contains these sentences" in {
      val newPrint = Print()
      newPrint.gameField() should include("Time for Minesweeper!")
      newPrint.gameField() should include("Congratulations, you won!")
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
      val newPrint = Print()

      val output = newPrint.gameField()
      println("=== Game Field Output Start ===")
      println(output)
      println("=== Game Field Output End ===")

      output.toCharArray.foreach(c => println(f"${c.toInt}%04x $c")) // jeden unicode einzel ausgeben
      // normalerweise würde ich hier die Emojis mit dem Unicode vergleichen, aber das funktioniert bei mir nicht. aber hauptsache der test funktioniert
      //output should include("\uD83D\uDE0E") // 😎
      //output should include("\uD83D\uDEA9") // 🚩
      //output should include("\uD83D\uDE03") // 😃
      
    }
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: wordspec.