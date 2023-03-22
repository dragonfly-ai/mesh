package shape

import ai.dragonfly.democrossy.Demonstration
import ai.dragonfly.mesh.io.PLY

object Screw extends Demonstration {

  override def demo():Unit = {
    println(ai.dragonfly.mesh.io.PLY.fromMesh(ai.dragonfly.mesh.shape.Screw(), PLY.randomVertexColorMapper))
  }

  override def name: String = "Screw"
}
