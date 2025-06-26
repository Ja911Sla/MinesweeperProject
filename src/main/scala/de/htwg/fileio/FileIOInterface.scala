package de.htwg.fileio

import de.htwg.model.BoardInterface

trait FileIOInterface {
  def save(board: BoardInterface): Unit
  def load(): BoardInterface
}
