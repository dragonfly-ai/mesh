package shape

import ai.dragonfly.democrossy.Demonstration

object Screw extends Demonstration {

  override def demo():Unit = {
    println(ai.dragonfly.mesh.io.PLY.fromMesh(ai.dragonfly.mesh.shape.Screw()))
  }

  override def name: String = "Screw"
}
