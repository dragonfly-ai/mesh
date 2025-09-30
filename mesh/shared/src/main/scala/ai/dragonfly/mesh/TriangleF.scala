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

package ai.dragonfly.mesh

import narr.*

import slash.vectorf.*
import slash.vectorf.VecF.*

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}


@JSExportTopLevel("TriangleF") @JSExportAll
object TriangleF {

  def nonZeroArea(p0:VecF[3], p1:VecF[3], p2:VecF[3]): Boolean = (p1 - p0).cross(p2 - p0).magnitudeSquared > 0

  def fromQuad(v1: Int, v2: Int, v3: Int, v4: Int):NArray[TriangleF] = {
    val out:NArray[TriangleF] = new NArray[TriangleF](2)
    out(0) = TriangleF(v1, v2, v3)
    out(1) = TriangleF(v1, v3, v4)
    out
  }
}

@JSExportAll
case class TriangleF(v1: Int, v2: Int, v3: Int) {
  def offset(delta: Int): TriangleF = TriangleF(v1 + delta, v2 + delta, v3 + delta)

  def nonZeroArea(points: NArray[VecF[3]]):Boolean = TriangleF.nonZeroArea(points(v1), points(v2), points(v3))

  def area(vertices: NArray[VecF[3]]): Double = {
    0.5 * (vertices(v2) - vertices(v1)).cross(vertices(v3) - vertices(v1)).magnitude
  }

  def boundsF(vertices: NArray[VecF[3]]): VectorFBounds[3] = VectorFBounds[3]( // min: V, MAX: V
    VecF[3](
      Math.min(vertices(v1).x, Math.min(vertices(v2).x, vertices(v3).x)),
      Math.min(vertices(v1).y, Math.min(vertices(v2).y, vertices(v3).y)),
      Math.min(vertices(v1).z, Math.min(vertices(v2).z, vertices(v3).z))
    ),
    VecF[3](
      Math.max(vertices(v1).x, Math.max(vertices(v2).x, vertices(v3).x)),
      Math.max(vertices(v1).y, Math.max(vertices(v2).y, vertices(v3).y)),
      Math.max(vertices(v1).z, Math.max(vertices(v2).z, vertices(v3).z))
    )
  )

  def toTriangle: Triangle = Triangle(v1, v2, v3)

  override def toString: String = s"TriangleF($v1, $v2, $v3)"
}
