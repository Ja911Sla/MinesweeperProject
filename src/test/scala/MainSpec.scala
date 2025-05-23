
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}
import scala.Console

class MainSpec extends AnyWordSpec {

  "The Main object" should {
    "run without throwing exceptions" in {
      val in = new ByteArrayInputStream("1\nQ\n".getBytes())
      val out = new ByteArrayOutputStream()

      Console.withIn(in) {
        Console.withOut(new PrintStream(out)) {
          main()
        }
      }

      val output = out.toString
      output should include("Willkommen zu Minesweeper!")
      output should include("Game over.")
    }
  }
}