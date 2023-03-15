package ai.dragonfly.mesh.shape

import narr.*
import ai.dragonfly.math.vector.*
import ai.dragonfly.mesh.*
object Plane {

  def apply(c0: Vector3, c1: Vector3, c2: Vector3, verticalSegments:Int = 1, horizontalSegments:Int = 1): Mesh = {

    val vX:Vector3 = c1 - c0
    val vY:Vector3 = c2 - c0

    val width:Int = horizontalSegments + 1
    val height:Int = verticalSegments + 1

    val pointCount: Int = width * height

    val points: NArray[Vector3] = new NArray[Vector3](pointCount)

    var y:Int = 0
    var x:Int = 0
    var p:Int = 0

    while (p < pointCount) {

      val dx:Double = x.toDouble / horizontalSegments
      val dy:Double = y.toDouble / verticalSegments

      points(p) = c0 + (vX * dx) + (vY * dy)

      x = x + 1
      if (x > width) {
        x = 0
        y += 1
      }
      p = y * width + x
    }

    val triangleCount:Int = 2 * verticalSegments * horizontalSegments
    val triangles: NArray[Triangle] = new NArray[Triangle](triangleCount)

    x = 0
    y = 0
    p = 0

    var t:Int = 0; while (t < triangleCount) {

      t = addQuad(
        p,
        p + 1 + width,
        p + width,
        p + 1,
        triangles,
        t
      )

      x = x + 1
      if (x >= horizontalSegments) {
        x = 0
        y += 1
      }
      p = y * width + x
    }

    Mesh(points, triangles)
  }

}
