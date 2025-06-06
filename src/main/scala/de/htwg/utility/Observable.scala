package de.htwg.utility

trait Observer {
  def update: String
}

trait Observable {
  private var subscribers: Vector[Observer] = Vector() // privat da es sonst gegen die Enkapselung verstößt
  def add(s: Observer): Unit = subscribers = subscribers :+ s
  def remove(s: Observer): Unit = subscribers = subscribers.filterNot(o => o == s)

  def notifyObservers(): String = {
    subscribers.foreach(_.update)
    s"${subscribers.size} observers notified."
  }
}
