package ai.dragonfly.mesh.shape

import narr.*
import ai.dragonfly.math.vector.*
import ai.dragonfly.math.Constant.π
import ai.dragonfly.math.{cubeInPlace, squareInPlace}
import ai.dragonfly.mesh.*

object Cylinder {
  def apply(angularSegments: Int = 36, sideSegments: Int = 1, baseSegments: Int = 1, capSegments: Int = 1, radius: Double = 1.0, height: Double = 1.0): Mesh = {
    Drum.apply(angularSegments, sideSegments, baseSegments, capSegments, radius, radius, height)
  }
}
