import ai.dragonfly.democrossy.{NativeConsole, *}
import shape.*
/**
 * Created by clifton on 1/9/17.
 */

object Demo extends XApp(NativeConsole(id = "console", style = "width:100%; height:100px; padding: 8px; overflow: scroll;")) {

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
