package de.htwg.controller.strategy

import de.htwg.controller.factory
import de.htwg.controller.factory.BoardFactory

trait GameModeStrategy {
  def getBoardFactory(): BoardFactory
}

//object EasyStrategy extends GameModeStrategy {
//  override def getBoardFactory(): BoardFactory = de.htwg.factory.EasyBoardFactory
//}
//
//object MediumStrategy extends GameModeStrategy {
//  override def getBoardFactory(): BoardFactory = de.htwg.factory.MediumBoardFactory
//}
//
//object HardStrategy extends GameModeStrategy {
//  override def getBoardFactory(): BoardFactory = de.htwg.factory.HardBoardFactory
//}

object CustomStrategy extends GameModeStrategy {
  override def getBoardFactory(): BoardFactory = {
    factory.BoardFactory.getInstance

  }
}


