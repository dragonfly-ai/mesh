package shape

import ai.dragonfly.democrossy.Demonstration
import ai.dragonfly.mesh.io.PLY

object Cylinder extends Demonstration {

  override def demo():Unit = {
    println(ai.dragonfly.mesh.io.PLY.fromMesh(ai.dragonfly.mesh.shape.Cylinder()))
  }

  override def name: String = "Cylinder"
}
