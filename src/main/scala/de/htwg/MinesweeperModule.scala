/*package de.htwg

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import de.htwg.controller.ControllerInterface
import de.htwg.controller.controllerBase.Controller
import de.htwg.controller.factory.{BoardFactory, BoardFactoryInterface}

class MinesweeperModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    binder()
    bind(classOf[ControllerInterface]).to(classOf[Controller])
    bind(classOf[BoardFactory]).toInstance(BoardFactory.getInstance)
  }
}
// benutze mit given /using

 */