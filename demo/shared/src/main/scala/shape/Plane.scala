package shape

import ai.dragonfly.democrossy.Demonstration

import slash.vector.*
object Plane extends Demonstration {

  override def demo():Unit = {
    println(
      ai.dragonfly.mesh.io.PLY.fromMesh(
        ai.dragonfly.mesh.shape.Plane(
          Vec[3](0, 0, 1),
          Vec[3](4, 0, 2),
          Vec[3](0, 4, 3),
          9,
          12
        )
      )
    )
  }

  override def name: String = "Plane"
}
