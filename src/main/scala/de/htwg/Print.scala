package de.htwg

case class Print():
  
  val fl = "\uD83D\uDEA9"
  val sm = "\uD83D\uDE03"
  val won = "\uD83D\uDE0E"
  val lost = "\uD83D\uDE35"

  val row = 9
  val col = 9
  val mineField = Array.ofDim[Int](row, col) // 2D array of Ints

  val mineCount = 20 // mines
  val flag = "\uD83D\uDEA9" // flag
  val bomb = "\uD83D\uDCA3" // bomb
  val emptyField = "\u2B1C" // field
  val smStart = "\uD83D\uDE03" // smileyStart
  val smWon = "\uD83D\uDE0E" // smileyVictory
  val smLose = "\uD83D\uDE35"
  
  def gameField(): String =
    s"""Time for Minesweeper!
    
            _________________________________________
            |                                       |
            |     Bombs    Play again     Timer     |
            |    _______     _______     _______    |
            |    |     |     |     |     |     |    |
            |    |  0  |     | $won  |     | 155 |    |
            |    |_____|     |_____|     |_____|    |
            |    _______________________________    |
            |    |     |     |     |     |     |    |
            |    |  $fl |  2  |  2  |  4  |  X  |    |
            |    |_____|_____|_____|_____|_____|    |
            |    |     |     |     |     |     |    |
            |    |  1  |  2  |  $fl |  $fl |  $fl |    |
            |    |_____|_____|_____|_____|_____|    |
            |    |     |     |     |     |     |    |
            |    |  1  |  2  |  3  |  5  |  3  |    |
            |    |_____|_____|_____|_____|_____|    |
            |    |     |     |     |     |     |    |
            |    |  $fl |  2  |  $fl |  2  |  $fl |    |
            |    |_____|_____|_____|_____|_____|    |
            |_______________________________________|
    
            Congratulations, you won!
          """

