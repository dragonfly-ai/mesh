package shape

import ai.dragonfly.democrossy.Demonstration

object Bolt extends Demonstration {

  override def demo():Unit = {
    println(ai.dragonfly.mesh.io.PLY.fromMesh(ai.dragonfly.mesh.shape.Bolt()))
  }

  override def name: String = "Bolt"
}
