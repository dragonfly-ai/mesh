package ai.dragonfly.mesh.shape

import narr.*
import ai.dragonfly.math.vector.*
import ai.dragonfly.math.Constant.π
import ai.dragonfly.math.{cubeInPlace, squareInPlace}
import ai.dragonfly.mesh.*

object Cube {

  def corners(l: Double): NArray[NArray[Vector3]] = NArray[NArray[Vector3]](
    NArray[Vector3](Vector3(0, 0, 0), Vector3(l, 0, 0), Vector3(l, l, 0), Vector3(0, l, 0)),
    NArray[Vector3](Vector3(0, 0, l), Vector3(l, 0, l), Vector3(l, l, l), Vector3(0, l, l))
  )

  def cubePoints(l: Double, n: Int): NArray[Vector3] = {

    val basis = corners(l)

    val panelStride: Int = n - 1

    val stride: Int = 4 * panelStride
    val capStride: Int = panelStride - 1
    val capPointCount: Int = squareInPlace(capStride)

    val pointCount: Int = cubeInPlace(n) - cubeInPlace(capStride)
    val sidePointCount = pointCount - (2 * capPointCount) // points.length - (2 * capPointCount)

    val points: NArray[Vector3] = new NArray[Vector3](pointCount) // (cubeInPlace(n) - cubeInPlace(n - 2))  // n³ - (n-2)³

    for (p <- 0 until sidePointCount) {
      val i: Int = (p % stride) / panelStride

      val bottomLeft: Vector3 = basis(0)(i)
      val bottomRight: Vector3 = basis(0)((i + 1) % 4)

      val z: Double = l * ((p / stride) / panelStride.toDouble)
      val alpha: Double = (p % panelStride) / panelStride.toDouble

      points(p) = Vector3(0.0, 0.0, z) + (bottomLeft * (1.0 - alpha)) + (bottomRight * alpha)
    }

    val Δl: Double = l / (n - 1)
    var p: Int = sidePointCount

    for (yi <- 1 to capStride) {
      for (xi <- 1 to capStride) {
        val x: Double = xi * Δl
        val y: Double = yi * Δl
        points(p) = Vector3(x, y, 0)
        points(p + capPointCount) = Vector3(x, y, l)
        p += 1
      }
    }

    points
  }

  def minimal(l:Double = 1.0): Mesh = apply(l, 2) // apply(l, 1)

  def apply(l: Double = 1.0, n: Int = 64, name:String = "Cube"): Mesh = {

    if (n < 2) throw IllegalArgumentException("A cube must have at least two points on each line segment.")

    val points: NArray[Vector3] = cubePoints(l, n)

    val panelStride: Int = n - 1

    val stride: Int = 4 * panelStride

    val triangles: NArray[Triangle] = new NArray[Triangle](12 * squareInPlace(n - 1)) // 12(n-1)²

    var t: Int = 0

    inline def addQuad(a: Int, b: Int, c: Int, d: Int): Unit = if (t < triangles.length) {
      triangles(t) = Triangle(a, b, c)
      triangles(t + 1) = Triangle(a, d, b)
      t += 2
    }

    // [front)[right)[back)[left)
    val sidePointCount = cubeInPlace(n) - cubeInPlace(n - 2) - (2 * squareInPlace(n - 2))

    for (p <- 1 until sidePointCount - stride + 1) {

      if (p % stride == 0) addQuad(p, p - 1, p - stride, p - 1 + stride)
      else addQuad(p + stride, p - 1, p, p + stride - 1)

    }

    // caps

    val capStride: Int = panelStride - 1
    val capPointCount: Int = squareInPlace(capStride)

    for (p <- capStride + 1 until capPointCount) {
      val pi: Int = sidePointCount + p

      if (p % capStride != 0) {
        // (bottom)
        addQuad(pi - capStride, pi - 1, pi, pi - capStride - 1)

        // (top)
        val pj: Int = sidePointCount + p + capPointCount
        addQuad(pj - 1, pj - capStride, pj, pj - capStride - 1)
      }
    }

    // Connect Caps to Sides:

    if (n > 2) {
      // Bottom Corners:
      // Left Front Bottom
      addQuad(stride - 1, 1, 0, sidePointCount)

      // Front Bottom Right
      addQuad(panelStride, sidePointCount + capStride - 1, panelStride + 1, panelStride - 1)

      // Bottom Right Back
      var temp: Int = 2 * panelStride
      addQuad(temp + 1, temp - 1, sidePointCount + capPointCount - 1, temp)

      // Bottom Back Left
      temp = 3 * panelStride
      addQuad(sidePointCount + capPointCount - capStride, temp, temp - 1, temp + 1)

      // Top Corners:
      val topOffset: Int = sidePointCount + capPointCount

      // Left Front Top
      temp = sidePointCount - stride
      addQuad(temp + 1, sidePointCount - 1, temp, topOffset)

      // Front Top Right
      temp = sidePointCount - stride + panelStride
      addQuad(temp, topOffset + capStride - 1, temp - 1, temp + 1)

      // Top Right Back
      temp = sidePointCount - 2 * panelStride
      addQuad(temp - 1, temp + 1, points.length - 1, temp)

      // Top Back Left
      temp = sidePointCount - panelStride
      addQuad(temp, points.length - capStride, temp - 1, temp + 1)

      // connect sides to caps
      for (i <- 0 until capStride - 1) {
        // Bottom Front
        temp = sidePointCount + i
        addQuad(temp, i + 2, i + 1, temp + 1)

        // Bottom Right
        val ibr: Int = sidePointCount + capStride - 1 + (capStride * i)
        temp = panelStride + i + 1
        addQuad(ibr + capStride, temp, ibr, temp + 1)

        // Bottom Back
        val ibb: Int = 3 * panelStride - i - 1
        temp = sidePointCount + capPointCount - capStride + i
        addQuad(ibb, temp + 1, temp, ibb - 1)

        // Bottom Left
        val ibl: Int = stride - i - 1
        temp = sidePointCount + (i * capStride)
        addQuad(ibl - 1, temp, ibl, temp + capStride)

        // Top Front
        val itf: Int = sidePointCount - stride + 1 + i
        temp = topOffset + i
        addQuad(temp, itf + 1, temp + 1, itf)

        // Top Right
        val itr: Int = sidePointCount - stride + panelStride + i + 1
        temp = topOffset + (i + 1) * capStride
        addQuad(itr, temp + capStride - 1, temp - 1, itr + 1)

        // Top Back
        val itb: Int = sidePointCount - panelStride - 1 - i
        temp = points.length - capStride + i
        addQuad(itb, temp + 1, itb - 1, temp)

        // Top Left
        val itl: Int = sidePointCount - i - 1
        temp = topOffset + i * capStride
        addQuad(temp, itl - 1, itl, temp + capStride)
      }
    } else {
      addQuad(0, 2, 1, 3)
      addQuad(7, 5, 6, 4)
    }

    Mesh(points, triangles, name)
  }
}
