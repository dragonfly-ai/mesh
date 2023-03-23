package shape

import ai.dragonfly.democrossy.Demonstration
import ai.dragonfly.mesh.io.PLY

object Bolt extends Demonstration {

  override def demo():Unit = {
    println(ai.dragonfly.mesh.io.PLY.fromMesh(ai.dragonfly.mesh.shape.Bolt(), PLY.randomVertexColorMapper))
  }

  override def name: String = "Bolt"
}
