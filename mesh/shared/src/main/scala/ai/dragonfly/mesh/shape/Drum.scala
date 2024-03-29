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

import ai.dragonfly.mesh.*

import scala.scalajs.js.annotation.JSExportTopLevel

object Drum {

  @JSExportTopLevel("Drum")
  def apply(
    angularSegments: Int = 12, sideSegments: Int = 1, baseSegments:Int = 1, capSegments: Int = 1,
    baseRadius: Double = 2.0, capRadius: Double = 1.0, height: Double = 1.0, name:String = "Drum"
  ): Mesh = {

    if (angularSegments < 3) throw new IllegalArgumentException("Drum doesn't support radial segment values less than 3.")
    if (sideSegments < 1 || baseSegments < 1 || capSegments < 1) throw new IllegalArgumentException("Drum doesn't support 0 segment values.")
    if (height < 0.0 || baseRadius < 0.0 || capRadius < 0.0) throw new IllegalArgumentException("Drum doesn't support negative dimension values.")

    val Δθ: Double = (2 * π) / angularSegments
    val cuts: Int = 2 + baseSegments + sideSegments + capSegments - 3

    val points: NArray[Vec[3]] = NArray.ofSize[Vec[3]](2 + (cuts * angularSegments))
    //  println(points.length)

    val pEnd: Int = points.length - 1

    points(0) = Vec[3](0, 0, 0)
    points(pEnd) = Vec[3](0, 0, height)

    var dT = 0.0
    var p = 1
    var pcount = 2;

    var cut:Int = 0; while (cut < cuts) {

      cut += 1

      p = 1
      while (p <= angularSegments) {

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

        val cutOffset: Int = (cut - 1) * angularSegments
        points(cutOffset + p) = Vec[3](x, y, h)

        dT = p * Δθ
        p = p + 1

      }
      pcount += p - 1

    }

    //    println(pcount)

    val triangles: NArray[Triangle] = new NArray[Triangle](
      2 * angularSegments * ( baseSegments + capSegments + sideSegments - 1 )
    )

    p = 1
    var t = 0

    val coEnd: Int = pEnd - angularSegments - 1 // cuts * radialSegments
    while (p <= angularSegments) {
      val ps: Int = p % angularSegments
      triangles(t) = Triangle(0, ps + 1, p) // bottom
      triangles(t + 1) = Triangle(pEnd, coEnd + p, coEnd + ps + 1) // top
      t += 2
      p += 1
    }

    cut = 0; while (cut < cuts) {

      val cutOffset: Int = cut * angularSegments

      p = 1

      while (p <= angularSegments && t < triangles.length) {
        val ps: Int = p % angularSegments
        val off2 = cutOffset + angularSegments

        t = addQuad(
          off2 + p,
          cutOffset + ps + 1,
          off2 + ps + 1,
          cutOffset + p,
          triangles,
          t
        )

        p += 1
      }
      cut += 1
    }

    println(s"triangles.length = ${triangles.length}, and t = $t")

    new Mesh(points, triangles, name)
  }
}
