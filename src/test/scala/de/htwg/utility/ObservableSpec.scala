package de.htwg.utility

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ObservableSpec extends AnyWordSpec with Matchers {

  class TestObservable extends Observable

  class TestObserver extends Observer {
    var updated = false

    override def update: String = {
      updated = true
      "updated"
    }
  }

  "An Observable" should {
    "notify added observers" in {
      val observable = new TestObservable
      val observer = new TestObserver

      observable.add(observer)
      observable.notifyObservers()

      observer.updated shouldBe true
    }

    "not notify removed observers" in {
      val observable = new TestObservable
      val observer = new TestObserver

      observable.add(observer)
      observable.remove(observer)
      observable.notifyObservers()

      observer.updated shouldBe false
    }
  }
}
