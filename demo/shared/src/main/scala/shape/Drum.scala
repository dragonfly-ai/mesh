package shape

import ai.dragonfly.democrossy.Demonstration

object Drum extends Demonstration {

  override def demo():Unit = {
    println(ai.dragonfly.mesh.io.PLY.fromMesh(ai.dragonfly.mesh.shape.Drum()))
  }

  override def name: String = "Drum"
}
