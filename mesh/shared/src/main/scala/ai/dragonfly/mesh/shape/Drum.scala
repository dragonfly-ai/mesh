package ai.dragonfly.mesh.shape

import narr.*
import ai.dragonfly.math.vector.*
import ai.dragonfly.math.Constant.π
import ai.dragonfly.math.{cubeInPlace, squareInPlace}
import ai.dragonfly.mesh.*

object Drum {

  def apply(
    radialSegments: Int = 18, sideSegments: Int = 1, baseSegments:Int = 1, capSegments: Int = 1,
    baseRadius: Double = 1.0, capRadius: Double = 1.0, height: Double = 1.0, name:String = "Drum"
  ): Mesh = {

    val Δθ: Double = (2 * π) / radialSegments
    val cuts: Int = 2 + baseSegments + sideSegments + capSegments - 3

    val points: NArray[Vector3] = new NArray[Vector3](2 + (cuts * radialSegments))
    //  println(points.length)

    val pEnd: Int = points.length - 1

    points(0) = Vector3(0, 0, 0)
    points(pEnd) = Vector3(0, 0, height)

    var dT = 0.0
    var p = 1
    var pcount = 2;

    var cut:Int = 0; while (cut < cuts) {

      cut += 1

      p = 1
      while (p <= radialSegments) {

        var r:Double = baseRadius
        var h:Double = 0

        if (cut < baseSegments) {
          val w:Double = cut.toDouble / baseSegments.toDouble
          r = baseRadius * w
          //(cut.toDouble / sideSegments.toDouble) * height)
        } else if (cut == baseSegments) {
          r = baseRadius
        } else if (cut < baseSegments + sideSegments) {
          val w:Double = (cut - baseSegments).toDouble / sideSegments.toDouble
          r = w * baseRadius + (1.0 - w) * capRadius
          h = w * height
        } else if (cut == baseSegments + sideSegments) {
//          val w:Double = (cut - baseSegments).toDouble / sideSegments.toDouble
          r = capRadius
          h = height
        } else {
          val w:Double = 1.0 - ((cut - (baseSegments + sideSegments)).toDouble / capSegments.toDouble)
          r = capRadius * w
          h = height
        }

        val x: Double = r * Math.cos(dT)
        val y: Double = r * Math.sin(dT)

        val cutOffset: Int = (cut - 1) * radialSegments
        points(cutOffset + p) = Vector3(x, y, h)

        dT = p * Δθ
        p = p + 1

      }
      pcount += p - 1

    }

    //    println(pcount)

    println(s"|old triangle| = ${(2 * (cuts + 1)) * radialSegments}")
    println(s"|new triangle 1| = ${2 * radialSegments * ( sideSegments + baseSegments + capSegments) - 4}")
    println(s"|new triangle 2| = ${2 * radialSegments * ( sideSegments + baseSegments + capSegments)}")
    val triangles: NArray[Triangle] = new NArray[Triangle](
      //(2 * (cuts + 1)) * radialSegments
      //2 * radialSegments * ( sideSegments + baseSegments + capSegments) - 4
      2 * radialSegments * ( sideSegments + baseSegments + capSegments)
    )

    p = 1
    var t = 0

    val coEnd: Int = pEnd - radialSegments - 1 // cuts * radialSegments
    while (p <= radialSegments) {
      val ps: Int = p % radialSegments
      triangles(t) = Triangle(0, ps + 1, p) // bottom
      triangles(t + 1) = Triangle(pEnd, coEnd + p, coEnd + ps + 1) // top
      t += 2
      p += 1
    }

    cut = 0; while (cut < cuts) {

      val cutOffset: Int = cut * radialSegments

      p = 1

      while (p <= radialSegments && t < triangles.length) {
        val ps: Int = p % radialSegments
        val off2 = cutOffset + radialSegments
        triangles(t) = Triangle(cutOffset + p, cutOffset + ps + 1, off2 + ps + 1) // side
        triangles(t + 1) = Triangle(off2 + p, cutOffset + p, off2 + ps + 1) // side
        t += 2
        p += 1
      }
      cut += 1
    }

    Mesh(points, triangles, name)
  }
}
