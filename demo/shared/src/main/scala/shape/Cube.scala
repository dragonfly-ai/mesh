package shape

import ai.dragonfly.democrossy.Demonstration

object Cube extends Demonstration {

  override def demo():Unit = {
    println(
      ai.dragonfly.mesh.io.PLY.fromMesh(ai.dragonfly.mesh.shape.Cube.minimal(1.0)) //, "Cube")
      //ai.dragonfly.mesh.io.PLY.fromMesh(ai.dragonfly.mesh.shape.Cube(1.0, 4)) //, "Cube")
    )
  }

  override def name: String = "Cube"
}
