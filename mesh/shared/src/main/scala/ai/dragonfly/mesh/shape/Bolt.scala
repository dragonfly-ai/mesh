package ai.dragonfly.mesh.shape

import ai.dragonfly.math.Constant.π
import ai.dragonfly.math.vector.Vector3
import ai.dragonfly.mesh.{Mesh, Triangle}
import narr.*

import scala.scalajs.js.annotation.JSExportTopLevel

object Bolt {
  @JSExportTopLevel("Bolt")
  def apply(
    length: Double = 10.0, threadsPerUnit: Double = 3.0, threadThickness: Double = 0.1, shankLength: Double = 3.5,
    angularSegments: Int = 36, threadRadius: Double = 1.0, coreRadius: Double = 0.85, name: String = "Bolt"
  ): Mesh = {

    // calculate cardinality of the set of points:

    val threadedLength: Double = length - shankLength

    val shankPointCount: Int = if (shankLength > 0.0) 3 * angularSegments else 0
    val threadPointCount: Int = 4 * ((threadedLength - threadThickness) * threadsPerUnit).toInt * angularSegments

    // populate set of points
    val points: NArray[Vector3] = new NArray[Vector3](shankPointCount + threadPointCount + 3)

    var p: Int = 0
    var dTheta: Double = 2 * π / angularSegments
    var theta: Double = 0.0

    // generate shank

    val zmsl: Double = length - shankLength

    while (p < angularSegments) {

      val x: Double = threadRadius * Math.cos(theta)
      val y: Double = threadRadius * Math.sin(theta)

      points(p) = Vector3(x, y, length)
      points(p + angularSegments) = Vector3(x, y, zmsl)

      points(p + 2 * angularSegments) = Vector3(
        coreRadius * Math.cos(theta),
        coreRadius * Math.sin(theta),
        zmsl
      )

      p += 1
      theta += dTheta
    }

    p += 2 * angularSegments

    val dZ: Double = 1.0 / (threadsPerUnit * angularSegments)
    var z: Double = threadedLength - dZ

    points(p) = Vector3(
      threadRadius * Math.cos(theta), // x
      threadRadius * Math.sin(theta), // y
      threadedLength - threadThickness
    )

    p += 1

    points(p) = Vector3(
      coreRadius * Math.cos(theta), // x
      coreRadius * Math.sin(theta), // y
      threadedLength - threadThickness
    )

    p += 1

    theta += dTheta

    // point and threaded core

    while (p + 3 < points.length) {

      val z1: Double = z - threadThickness

      points(p) = Vector3(
        coreRadius * Math.cos(theta), // x
        coreRadius * Math.sin(theta), // y
        z
      )
      p += 1

      val tx: Double = threadRadius * Math.cos(theta)
      val ty: Double = threadRadius * Math.sin(theta)

      points(p) = Vector3(tx, ty, z)
      p += 1

      points(p) = Vector3(tx, ty, z1)
      p += 1

      points(p) = Vector3(
        coreRadius * Math.cos(theta), // x
        coreRadius * Math.sin(theta), // y
        z1
      )
      p += 1

      theta += dTheta
      z -= dZ
    }

    points(points.length - 1) = Vector3(0.0, 0.0, 0.0)

    val triangles: NArray[Triangle] = new NArray[Triangle](7 * angularSegments + 2 * threadPointCount + 3)

    p = 0
    var t: Int = 0

    // shank triangles

    while (t < 2 * (angularSegments - 1)) {
      t = addQuad(p, p + angularSegments + 1, p + 1, p + angularSegments, triangles, t)
      p += 1
    }

    t = addQuad(angularSegments, angularSegments - 1, p + angularSegments, 0, triangles, t)

    p += 1

    while (t < 4 * angularSegments - 2) {
      t = addQuad(p, p + angularSegments + 1, p + 1, p + angularSegments, triangles, t)
      p += 1
    }

    // weld thread to shank

    triangles(t) = Triangle(p + 1 + angularSegments, p + 1 - angularSegments, p)
    t += 1

    t = addQuad(p + 1 + angularSegments, p + angularSegments, p + 2 + angularSegments, p, triangles, t)

    // first thread segment
    t = addQuad(p + 3 + angularSegments, p + 1 - angularSegments, p + 4 + angularSegments, p + 1, triangles, t)
    t = addQuad(p + 4 + angularSegments, p + 1 + angularSegments, p + 5 + angularSegments, p + 1 - angularSegments, triangles, t)
    t = addQuad(p + 5 + angularSegments, p + 2 + angularSegments, p + 6 + angularSegments, p + 1 + angularSegments, triangles, t)

    // first core triangle
    triangles(t) = Triangle(
      p + 3 + angularSegments,
      p + 2,
      p + 1
    )
    t += 1

    // first core spiral
    var i: Int = 0
    var p0: Int = p + 2
    var p1: Int = p + 3 + angularSegments
    while (i < angularSegments - 2) {
      t = addQuad(
        p1 + 4,
        p0,
        p1,
        p0 + 1,
        triangles,
        t
      )
      i += 1
      p0 += 1
      p1 += 4
    }

    // last 2 core quads
    t = addQuad(p - 1 + 5 * angularSegments, p + angularSegments, p - 5 + 5 * angularSegments, p + 2 + angularSegments, triangles, t)
    t = addQuad(
      p + 3 + 5 * angularSegments,
      p + 2 + angularSegments,
      p - 1 + 5 * angularSegments,
      p + 6 + angularSegments,
      triangles,
      t
    )

    p = 3 * angularSegments + 2

    while (p + 4 < points.length - 1) {

      // thread
      t = addQuad(p, p + 5, p + 4, p + 1, triangles, t)
      t = addQuad(p + 1, p + 6, p + 5, p + 2, triangles, t)
      t = addQuad(p + 2, p + 7, p + 6, p + 3, triangles, t)

      // core
      t = addQuad(p + 4 * angularSegments + 4, p + 3, p + 4 * angularSegments, p + 7, triangles, t)

      p += 4
    }

    // thread cap
    t = addQuad(p + 3, p + 1, p + 2, p, triangles, t)

    val last: Int = points.length - 1
    p0 = last - 1
    p1 = p0 - 4
    while (p0 > last - 4 * angularSegments) {
      triangles(t) = Triangle(last, p0, p1)
      t += 1
      p0 -= 4
      p1 -= 4
    }

    triangles(t) = Triangle(last - 4, last - 1, last)

    Mesh(points, triangles, name)
  }

}
