package de.htwg

case class Field(x: Int, y: Int, isBomb: Boolean = false):
  def informationField(): String =
    if (!isBomb) {
      s"X: $x\nY: $y\nBomb: false"
    } else {
      s"X: $x\nY: $y\nBomb: true"
    }
  def computeAmountOfFields(): Int =
    x * y
  def hasBomb(): Boolean =
    isBomb
