package ai.dragonfly.mesh.shape

import narr.*
import ai.dragonfly.math.vector.*
import ai.dragonfly.math.Constant.π
import ai.dragonfly.math.{cubeInPlace, squareInPlace}
import ai.dragonfly.mesh.*

object Cylinder {
  def apply(radialSegments: Int = 18, sideSegments: Int = 2, baseSegments: Int = 2, capSegments: Int = 2, radius: Double = 1.0, height: Double = 1.0): Mesh = {
    Drum.apply(radialSegments, sideSegments, baseSegments, capSegments, radius, radius, height)
  }
}
