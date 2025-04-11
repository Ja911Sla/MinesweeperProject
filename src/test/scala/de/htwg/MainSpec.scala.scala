import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import java.io.{ByteArrayOutputStream, PrintStream}
import de.htwg._

class MainSpec extends AnyWordSpec {
  "The main method" should {
    "print the game field and a welcome message" in {
      // Capture stdout
      val outCapture = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outCapture)) {
        main() // call your main method
      }

      val output = outCapture.toString

      // Assert things that should appear in the output
      output should include ("It works")
      output should include ("Time for Minesweeper!")
      output should include ("Congratulations, you won!")
    }
  }
}