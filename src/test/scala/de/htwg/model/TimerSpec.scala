package de.htwg.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class TimerSpec extends AnyWordSpec {

  "A Timer" should {

    "start only once and set running to true" in {
      val timer = new Timer
      timer.start() should be (true)  // Should start successfully the first time
      timer.start() should be (false) // Should not start again
      timer.isRunning should be (true)
    }

    "increment elapsed time when running" in {
      val timer = new Timer
      timer.start()
      Thread.sleep(2100) // Give it about 2 seconds
      timer.getTime should be >= 2
    }

    "stop and report its previous state" in {
      val timer = new Timer
      timer.start()
      Thread.sleep(1000) // Give it a moment to run
      timer.stop() should be (true)
      timer.isRunning should be (false)
    }

    "reset the elapsed time to zero and return previous value" in {
      val timer = new Timer
      timer.start()
      Thread.sleep(2100) // Let it run for about 2 seconds
      timer.stop()
      val elapsedBeforeReset = timer.reset()
      elapsedBeforeReset should be >= 2
      timer.getTime should be (0)
    }

    "report zero elapsed time initially" in {
      val timer = new Timer
      timer.getTime should be (0)
    }

    "report not running initially" in {
      val timer = new Timer
      timer.isRunning should be (false)
    }
  }
}

