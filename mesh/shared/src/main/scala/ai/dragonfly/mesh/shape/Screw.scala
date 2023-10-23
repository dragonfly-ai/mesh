/*
 * Copyright 2023 dragonfly.ai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.dragonfly.mesh.shape

import narr.*

import scala.language.implicitConversions

import slash.Constant.π
import slash.vector.*
import Vec.*
import ai.dragonfly.mesh.{Mesh, Triangle}

import scala.scalajs.js.annotation.*

object Screw {

  /**
   * Generate a 3D mesh of a threaded screw.
   * @param length The total length of the entire screw. Must be greater than 0.0.
   * @param threadsPerUnit How many thread revolutions complete per unit of distance along the screw.
   * @param threadThickness The thickness of the thread.  Must be within the range of: (0.0, 1.0 / threadsPerUnit].
   * @param shankLength How much of the length of the screw to allocate to the shank.  Must be within the range of: (0.0, length - pointLength].
   * @param pointLength How much of the length of the screw to allocate to the point.  The point steps linearly from thread radius to 0.
   * @param angularSegments How many segments to approximate the circle.  Must be greater than 2.
   * @param threadRadius The maximum radius of the entire screw.  Must be greater than coreRadius.
   * @param coreRadius The radius of the core.  Must be within the range of: (0.0, threadRadius).
   * @param name The name of the mesh.
   * @return a 3d Mesh of a screw that reflects the specifications given by the parameters.
   */
  @JSExportTopLevel("Screw")
  def apply(
    length:Double = 7.0, threadsPerUnit:Double = 2.0, threadThickness:Double = 0.05, shankLength:Double = 1.5,
    pointLength:Double = 0.5, angularSegments:Int = 12, threadRadius:Double = 0.375, coreRadius:Double = 0.25,
    name:String = "Screw"
  ): Mesh = {

    // calculate cardinality of the set of points:

    val threadedLength:Double = length - shankLength

    val shankPointCount:Int = if (shankLength > 0.0) 3 * angularSegments else 0
    val threadPointCount:Int = 4 * ((threadedLength - threadThickness) * threadsPerUnit).toInt * angularSegments

    // populate set of points
    val points: NArray[Vec[3]] = NArray.ofSize[Vec[3]]( shankPointCount + threadPointCount + 3 )
    val lastPointIndex:Int = points.length - 1

    var p:Int = 0
    val dTheta:Double = 2 * π / angularSegments
    var theta:Double = 0.0

    // generate shank

    val zmsl:Double = length - shankLength

    while (p < angularSegments) {

      val x:Double = threadRadius * Math.cos(theta)
      val y:Double = threadRadius * Math.sin(theta)

      points(p) = Vec[3]( x, y, length )
      points(p + angularSegments) = Vec[3]( x, y, zmsl )

      points(p + 2 * angularSegments) = Vec[3](
        coreRadius * Math.cos(theta),
        coreRadius * Math.sin(theta),
        zmsl
      )

      p += 1
      theta += dTheta
    }

    p += 2 * angularSegments

    val dZ:Double = 1.0 / (threadsPerUnit * angularSegments)
    var z:Double = threadedLength - dZ

    points(p) = Vec[3](
      threadRadius * Math.cos(theta), // x
      threadRadius * Math.sin(theta), // y
      threadedLength - threadThickness
    )

    p += 1

    points(p) = Vec[3](
      coreRadius * Math.cos(theta), // x
      coreRadius * Math.sin(theta), // y
      threadedLength - threadThickness
    )

    p += 1

    theta += dTheta

    var pntAlpha: Double = 0.0

    // point and threaded core

    while (p + 3 < points.length) {

      pntAlpha = if (z < pointLength) 1.0 - ((pointLength - z) / pointLength) else 1.0

      val z1: Double = z - threadThickness

      points(p) = Vec[3](
        pntAlpha * coreRadius * Math.cos(theta), // x
        pntAlpha * coreRadius * Math.sin(theta), // y
        z
      )
      p += 1

      pntAlpha = if (z1 < pointLength) 1.0 - ((pointLength - z1) / pointLength) else 1.0

      val tx: Double = pntAlpha * threadRadius * Math.cos(theta)
      val ty: Double = pntAlpha * threadRadius * Math.sin(theta)

      points(p) = Vec[3](tx, ty, z)
      p += 1

      points(p) = Vec[3](tx, ty, z1)
      p += 1

      points(p) = Vec[3](
        pntAlpha * coreRadius * Math.cos(theta), // x
        pntAlpha * coreRadius * Math.sin(theta), // y
        z1
      )
      p += 1

      theta += dTheta
      z -= dZ
    }

    points(lastPointIndex) = Vec[3](0.0, 0.0, 0.0)

    val triangles: NArray[Triangle] = new NArray[Triangle](6 * angularSegments + 2 * threadPointCount - (angularSegments - 4))

    p = 0
    var t:Int = 0

    // shank triangles

    while (t < 2 * (angularSegments - 1)) {
      t = addQuad(p, p + angularSegments + 1, p + 1,  p + angularSegments, triangles, t)
      p += 1
    }

    t = addQuad(angularSegments, angularSegments - 1, p + angularSegments, 0, triangles, t)

    p += 1

    while (t < 4 * angularSegments - 2) {
      t = addQuad(p, p + angularSegments + 1, p + 1,  p + angularSegments, triangles, t)
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
    var i:Int = 0
    var p0:Int = p + 2
    var p1:Int = p + 3 + angularSegments
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

    // lastPointIndex 2 core quads
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

    while (p + 7 < lastPointIndex) {

      // thread
      t = addQuad(p, p + 5, p + 4, p + 1, triangles, t)
      t = addQuad(p + 1, p + 6, p + 5, p + 2, triangles, t)
      t = addQuad(p + 2, p + 7, p + 6, p + 3, triangles, t)

      // core
      val lastCorePoint:Int = p + 4 * angularSegments + 4
      if (lastCorePoint < lastPointIndex) {
        t = addQuad(lastCorePoint, p + 3, lastCorePoint - 4, p + 7, triangles, t)
      }

      p += 4
    }

    // thread cap
    t = addQuad(p + 3, p + 1, p + 2, p, triangles, t)

    p0 = lastPointIndex - 1
    p1 = p0 - 4
    while (p0 > lastPointIndex - 4 * angularSegments) {
      triangles(t) = Triangle(lastPointIndex, p0, p1)
      t += 1
      p0 -= 4
      p1 -= 4
    }

    triangles(t) = Triangle(lastPointIndex - 4, lastPointIndex - 1, lastPointIndex - 4 - (4 * angularSegments))
    t += 1
    triangles(t) = Triangle(lastPointIndex - 4 - (4 * angularSegments), lastPointIndex - 1, lastPointIndex)

    println(s"t = $t and triangles.length = ${triangles.length}")

    new Mesh(points, triangles, name)
  }

}
