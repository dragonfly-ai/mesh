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

import slash.vector.*
import Vec.*

import scala.collection.mutable
import scala.scalajs.js.annotation.{JSExportAll, *}


@JSExportTopLevel("Mesh") @JSExportAll
object Mesh {

  def fromPointsAndHashSet(points:NArray[Vec[3]], triangleSet:mutable.HashSet[Triangle], name:String):Mesh = {
    val triangles:NArray[Triangle] = new NArray[Triangle](triangleSet.size)
    var i:Int = 0
    for (t <- triangleSet) {
      triangles(i) = t
      i += 1
    }
    new Mesh(points, triangles, name)
  }
  def combine(name: String, meshes: Mesh*): Mesh = {
    var pointCount = 0
    var polygonCount = 0

    for (m <- meshes) {
      pointCount = pointCount + m.points.length
      polygonCount = polygonCount + m.triangles.length
    }

    val points: NArray[Vec[3]] = NArray.ofSize[Vec[3]](pointCount)
    val triangles: NArray[Triangle] = new NArray[Triangle](polygonCount)

    var pi: Int = 0
    var tj = 0

    for (m <- meshes) {
      var pj = pi

      var p:Int = 0; while (p < m.points.length) {
        points(pj) = m.points(p)
        pj = pj + 1
        p += 1
      }

      for (polygon <- m.triangles) {
        triangles(tj) = polygon.offset(pi)
        tj = tj + 1
      }

      pi = pi + m.points.length
    }

    new Mesh(points, triangles, name)
  }
}

@JSExportAll
class Mesh(val points: NArray[Vec[3]], val triangles: NArray[Triangle], val name:String = "Untitled Mesh") {

  def scale(scalar: Double): Unit = {
    var p:Int = 0; while (p < points.length) {
      points(p).scale(scalar)
      p += 1
    }
  }

  def translate(offset:Vec[3]): Unit = {
    var p: Int = 0;
    while (p < points.length) {
      points(p).add(offset)
      p += 1
    }
  }

  def transform(f: Vec[3] => Vec[3]):Mesh = new Mesh(
    NArray.tabulate[Vec[3]](points.length)((i:Int) => f(points(i))),
    triangles
  )

  def copy(copyName:String = this.name):Mesh = new Mesh(
    NArray.tabulate[Vec[3]](points.length)((i:Int) => points(i)),
    NArray.tabulate[Triangle](triangles.length)((i:Int) => triangles(i)),
    copyName
  )

  override def toString: String = {
    val sb:StringBuilder = new StringBuilder()
    sb.append("Mesh Data {\n")
    sb.append("\tPoints {\n")
    var i:Int = 0; while (i < points.length) {
      sb.append("\t\t").append(i).append(" : ").append(points(i)).append("\n")
      i += 1
    }
    sb.append("\t}\n")
    sb.append("\tTriangles {\n")
    i = 0; while (i < triangles.length) {
      sb.append("\t\t").append(i).append(" : ").append(triangles(i)).append("\n")
      i += 1
    }
    i = 0;
    sb.append("\t}\n").append("}\n").toString()
  }
}