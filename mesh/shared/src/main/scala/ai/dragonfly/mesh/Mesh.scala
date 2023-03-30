package ai.dragonfly.mesh

import ai.dragonfly.math.vector.Vector3
import narr.NArray

import scala.collection.mutable
import scala.scalajs.js.annotation.{JSExportAll, *}
import scala.util.Random

@JSExportTopLevel("Mesh") @JSExportAll
object Mesh {

  def fromPointsAndHashSet(points:NArray[Vector3], triangleSet:mutable.HashSet[Triangle], name:String):Mesh = {
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

    val points: NArray[Vector3] = new NArray[Vector3](pointCount)
    val triangles: NArray[Triangle] = new NArray[Triangle](polygonCount)

    var pi: Int = 0
    var tj = 0

    for (m <- meshes) {
      var pj = pi

      for (p <- m.points) {
        points(pj) = p
        pj = pj + 1
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
class Mesh(val points: NArray[Vector3], val triangles: NArray[Triangle], val name:String = "Untitled Mesh") {

  def scale(scalar: Double): Unit = {
    for (p <- points) p.scale(scalar)
  }

  def translate(offset:Vector3): Unit = {
    for (p <- points) p.add(offset)
  }

  def transform(f: Vector3 => Vector3):Mesh = new Mesh( points.map(f), triangles )

  def copy(copyName:String = this.name):Mesh = new Mesh(
    NArray.tabulate[Vector3](points.length)((i:Int) => points(i)),
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