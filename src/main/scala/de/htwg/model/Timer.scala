package de.htwg.model;

class Timer {
  @volatile private var secondsElapsed = 0
  @volatile private var running = false
  private var threadStarted = false

  def start(): Boolean = {
    if (threadStarted) return false
    running = true
    threadStarted = true
    val timerThread = new Thread(() => {
      while (running) {
        Thread.sleep(1000)
        secondsElapsed += 1
      }
    })
    timerThread.setDaemon(true)
    timerThread.start()
    true
  }

  def stop(): Boolean = {
    val wasRunning = running
    running = false
    wasRunning
  }

  def reset(): Int = {
    val prev = secondsElapsed
    secondsElapsed = 0
    prev
  }

  def getTime: Int = secondsElapsed

  def isRunning: Boolean = running
}