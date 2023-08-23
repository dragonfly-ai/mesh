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
import Extensions.given
import scala.language.implicitConversions

import ai.dragonfly.math.vector.*
import Vec.*
import ai.dragonfly.mesh.*

import scala.scalajs.js.annotation.JSExportTopLevel
object Plane {

  /**
   * Create a rectangular plain from three points in 3D space.
   * @param c0 corner 1
   * @param c1 corner 2
   * @param c2 corner 3
   * @param verticalSegments vertical segment count
   * @param horizontalSegments horizontal segment count
   * @return
   */
  @JSExportTopLevel("Plane")
  def apply(c0: Vec[3], c1: Vec[3], c2: Vec[3], verticalSegments:Int = 1, horizontalSegments:Int = 1): Mesh = {

    val vX:Vec[3] = c1 - c0
    val vY:Vec[3] = c2 - c0

    val width:Int = horizontalSegments + 1
    val height:Int = verticalSegments + 1

    val pointCount: Int = width * height

    val points: NArray[Vec[3]] = NArray.ofSize[Vec[3]](pointCount)

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

    new Mesh(points, triangles)
  }

}
