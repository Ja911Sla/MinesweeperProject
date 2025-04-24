import java.io.ByteArrayOutputStream
import java.io.PrintStream
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import scala.Console.out

class MainSpec extends AnyWordSpec {
  "main method" should {
    "create this output" in {
      // Output der main Methode wird abgefangen und in Variable mainOut gespeichert
      val mainOut = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(mainOut)) {
        main()
      }
      val mainOutput = mainOut.toString.trim.split("\n").map(_.trim).toList

      mainOutput.head should include("It works")
      mainOutput.exists(_.contains("Time for Minesweeper!")) shouldBe true
      mainOutput.tail should (equal("true") or equal("false"))
    }
  }
}
