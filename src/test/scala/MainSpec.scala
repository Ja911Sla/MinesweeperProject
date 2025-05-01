import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.PrintStream
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

import scala.Console.out

class MainSpec extends AnyWordSpec {
  "main method" should {
    "create this output" in {
      // Output der main Methode wird abgefangen und in Variable mainOut gespeichert
      val mainIn = new ByteArrayInputStream("Q\n".getBytes()) // Simuliere Eingabe: Q = quit, ohne Q sonst endlos Schleife
      val mainOut = new ByteArrayOutputStream()

      Console.withIn(mainIn) { // Simuliere Eingabe
        Console.withOut(new PrintStream(mainOut)) { // Simuliere Ausgabe
          main()
        }
      }
      val mainOutput = mainOut.toString.trim.split("\n").map(_.trim).toList

      mainOutput.head should include("Willkommen zu Minesweeper!")
      //mainOutput.exists(_.contains("Time for Minesweeper!")) shouldBe true
      // mainOutput.tail should (equal("true") or equal("false"))
    }
  }
}
