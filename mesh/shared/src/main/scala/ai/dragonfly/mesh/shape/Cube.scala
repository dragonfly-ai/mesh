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

import ai.dragonfly.math.Constant.π
import ai.dragonfly.math.{cubeInPlace, squareInPlace}
import ai.dragonfly.mesh.*

import scala.scalajs.js.annotation.JSExportTopLevel

object Cube {

  def corners(l: Double): NArray[NArray[Vec[3]]] = {
    val out: NArray[NArray[Vec[3]]] = NArray.ofSize[NArray[NArray[Double]]](2).asInstanceOf[NArray[NArray[Vec[3]]]]
    out(0) = NArray[Vec[3]](Vec[3](0, 0, 0), Vec[3](l, 0, 0), Vec[3](l, l, 0), Vec[3](0, l, 0))
    out(1) = NArray[Vec[3]](Vec[3](0, 0, l), Vec[3](l, 0, l), Vec[3](l, l, l), Vec[3](0, l, l))
    out
  }

  def cubePoints(l: Double, n: Int): NArray[Vec[3]] = {

    val basis = corners(l)

    val panelStride: Int = n - 1

    val stride: Int = 4 * panelStride
    val capStride: Int = panelStride - 1
    val capPointCount: Int = squareInPlace(capStride)

    val pointCount: Int = cubeInPlace(n) - cubeInPlace(capStride)
    val sidePointCount = pointCount - (2 * capPointCount) // points.length - (2 * capPointCount)

    val points: NArray[Vec[3]] = NArray.ofSize[Vec[3]](pointCount) // (cubeInPlace(n) - cubeInPlace(n - 2))  // n³ - (n-2)³

    for (p <- 0 until sidePointCount) {
      val i: Int = (p % stride) / panelStride

      val bottomLeft: Vec[3] = basis(0)(i)
      val bottomRight: Vec[3] = basis(0)((i + 1) % 4)

      val z: Double = l * ((p / stride) / panelStride.toDouble)
      val alpha: Double = (p % panelStride) / panelStride.toDouble

      points(p) = Vec[3](0.0, 0.0, z) + (bottomLeft * (1.0 - alpha)) + (bottomRight * alpha)
    }

    val Δl: Double = l / (n - 1)
    var p: Int = sidePointCount

    for (yi <- 1 to capStride) {
      for (xi <- 1 to capStride) {
        val x: Double = xi * Δl
        val y: Double = yi * Δl
        points(p) = Vec[3](x, y, 0)
        points(p + capPointCount) = Vec[3](x, y, l)
        p += 1
      }
    }

    points
  }

  /**
   * Create a cube with minimal number of triangles and points.
   * @param l the length of the cube.
   * @return a triangular cube mesh.
   */
  @JSExportTopLevel("MinimalCube")
  def minimal(l:Double = 1.0): Mesh = apply(l, 2) // apply(l, 1)

  @JSExportTopLevel("Cube")
  def apply(l: Double = 1.0, n: Int = 64, name:String = "Cube"): Mesh = {

    if (n < 2) throw IllegalArgumentException("A cube must have at least two points on each line segment.")

    val points: NArray[Vec[3]] = cubePoints(l, n)

    val panelStride: Int = n - 1

    val stride: Int = 4 * panelStride

    val triangles: NArray[Triangle] = new NArray[Triangle](12 * squareInPlace(n - 1)) // 12(n-1)²

    var t: Int = 0

    // [front)[right)[back)[left)
    val sidePointCount = cubeInPlace(n) - cubeInPlace(n - 2) - (2 * squareInPlace(n - 2))

    for (p <- 1 until sidePointCount - stride + 1) {

      t = if (p % stride == 0) addQuad(p, p - 1, p - stride, p - 1 + stride, triangles, t)
      else addQuad(p + stride, p - 1, p, p + stride - 1, triangles, t)

    }

    // caps

    val capStride: Int = panelStride - 1
    val capPointCount: Int = squareInPlace(capStride)

    for (p <- capStride + 1 until capPointCount) {
      val pi: Int = sidePointCount + p

      if (p % capStride != 0) {
        // (bottom)
        t = addQuad(pi - capStride, pi - 1, pi, pi - capStride - 1, triangles, t)

        // (top)
        val pj: Int = sidePointCount + p + capPointCount
        t = addQuad(pj - 1, pj - capStride, pj, pj - capStride - 1, triangles, t)
      }
    }

    // Connect Caps to Sides:

    if (n > 2) {
      // Bottom Corners:
      // Left Front Bottom
      t = addQuad(stride - 1, 1, 0, sidePointCount, triangles, t)

      // Front Bottom Right
      t = addQuad(panelStride, sidePointCount + capStride - 1, panelStride + 1, panelStride - 1, triangles, t)

      // Bottom Right Back
      var temp: Int = 2 * panelStride
      t = addQuad(temp + 1, temp - 1, sidePointCount + capPointCount - 1, temp, triangles, t)

      // Bottom Back Left
      temp = 3 * panelStride
      t = addQuad(sidePointCount + capPointCount - capStride, temp, temp - 1, temp + 1, triangles, t)

      // Top Corners:
      val topOffset: Int = sidePointCount + capPointCount

      // Left Front Top
      temp = sidePointCount - stride
      t = addQuad(temp + 1, sidePointCount - 1, temp, topOffset, triangles, t)

      // Front Top Right
      temp = sidePointCount - stride + panelStride
      t = addQuad(temp, topOffset + capStride - 1, temp - 1, temp + 1, triangles, t)

      // Top Right Back
      temp = sidePointCount - 2 * panelStride
      t = addQuad(temp - 1, temp + 1, points.length - 1, temp, triangles, t)

      // Top Back Left
      temp = sidePointCount - panelStride
      t = addQuad(temp, points.length - capStride, temp - 1, temp + 1, triangles, t)

      // connect sides to caps
      for (i <- 0 until capStride - 1) {
        // Bottom Front
        temp = sidePointCount + i
        t = addQuad(temp, i + 2, i + 1, temp + 1, triangles, t)

        // Bottom Right
        val ibr: Int = sidePointCount + capStride - 1 + (capStride * i)
        temp = panelStride + i + 1
        t = addQuad(ibr + capStride, temp, ibr, temp + 1, triangles, t)

        // Bottom Back
        val ibb: Int = 3 * panelStride - i - 1
        temp = sidePointCount + capPointCount - capStride + i
        t = addQuad(ibb, temp + 1, temp, ibb - 1, triangles, t)

        // Bottom Left
        val ibl: Int = stride - i - 1
        temp = sidePointCount + (i * capStride)
        t = addQuad(ibl - 1, temp, ibl, temp + capStride, triangles, t)

        // Top Front
        val itf: Int = sidePointCount - stride + 1 + i
        temp = topOffset + i
        t = addQuad(temp, itf + 1, temp + 1, itf, triangles, t)

        // Top Right
        val itr: Int = sidePointCount - stride + panelStride + i + 1
        temp = topOffset + (i + 1) * capStride
        t = addQuad(itr, temp + capStride - 1, temp - 1, itr + 1, triangles, t)

        // Top Back
        val itb: Int = sidePointCount - panelStride - 1 - i
        temp = points.length - capStride + i
        t = addQuad(itb, temp + 1, itb - 1, temp, triangles, t)

        // Top Left
        val itl: Int = sidePointCount - i - 1
        temp = topOffset + i * capStride
        t = addQuad(temp, itl - 1, itl, temp + capStride, triangles, t)
      }
    } else {
      t = addQuad(0, 2, 1, 3, triangles, t)
      t = addQuad(7, 5, 6, 4, triangles, t)
    }

    new Mesh(points, triangles, name)
  }
}
