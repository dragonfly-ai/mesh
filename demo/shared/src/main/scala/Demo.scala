import ai.dragonfly.democrossy.{Demonstration, DivConsole, XApp}

import shape.*
/**
 * Created by clifton on 1/9/17.
 */

object Demo extends XApp(DivConsole(style = "padding: 8px; overflow: scroll;")) {

  val allDemos: Array[Demonstration] = Array[Demonstration](
    Cube,
    Cylinder,
    Drum,
    Screw,
    Bolt,
    Plane
  )
  def main(args: Array[String]): Unit = {
    for (d <- allDemos) d.demonstrate
    
    //ai.dragonfly.mesh.io.OBJ.fromMesh(Cube.)
  }

}
