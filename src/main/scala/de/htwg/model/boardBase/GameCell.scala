package de.htwg.model.boardBase

// alle infos die in einer Zelle gespeichert werden
case class GameCell(
                     var isMine: Boolean = false, // Mine in der Zelle ja oder nein
                     var isRevealed: Boolean = false, //sieht man die Mine oder nicht
                     var isFlagged: Boolean = false, // ist sie makiert ?
                     var mineCount: Int = 0)
