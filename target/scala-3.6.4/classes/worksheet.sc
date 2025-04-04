
val row = 9
val col = 9
val mineField = Array.ofDim[Int](row, col) // 2D array of Ints

val mineCount = 20          // mines
val flag = "\uD83D\uDEA9"   // flag
val bomb = "\uD83D\uDCA3"   // bomb
val emptyField = "\u2B1C"   // field
val smStart = "\uD83D\uDE03"     // smileyStart
val smWon = "\uD83D\uDE0E"  // smileyVictory
val smLose = "\uD83D\uDE35"