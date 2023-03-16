package ai.dragonfly.mesh

import ai.dragonfly.math.vector.Vector3
import narr.NArray

import scala.scalajs.js.annotation.{JSExportAll, *}
import scala.util.Random

@JSExportTopLevel("Mesh") @JSExportAll
object Mesh {
  def combine(name: String, meshes: Mesh*): Mesh = {
    var pointCount = 0
    var polygonCount = 0

    for (m <- meshes) {
      pointCount = pointCount + m.points.length
      polygonCount = polygonCount + m.triangles.length
    }

    val points: NArray[Vector3] = new NArray[Vector3](pointCount)
    val polygons: NArray[Triangle] = new NArray[Triangle](polygonCount)

    var pi: Int = 0
    var tj = 0

    for (m <- meshes) {
      var pj = pi

      for (p <- m.points) {
        points(pj) = p
        pj = pj + 1
      }

      for (polygon <- m.triangles) {
        polygons(tj) = polygon.offset(pi)
        tj = tj + 1
      }

      pi = pi + m.points.length
    }

    Mesh(points, polygons)
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


@JSExportTopLevel("Triangle") @JSExportAll
object Triangle {
  def nonZeroArea(p0:Vector3, p1:Vector3, p2:Vector3): Boolean = (p1 - p0).cross(p2 - p0).magnitudeSquared > 0

  def fromQuad(v1: Int, v2: Int, v3: Int, v4: Int):NArray[Triangle] = {
    val out:NArray[Triangle] = new NArray[Triangle](2)
    out(0) = Triangle(v1, v2, v3)
    out(1) = Triangle(v1, v3, v4)
    out
  }
}

@JSExportAll
case class Triangle(v1: Int, v2: Int, v3: Int) {
  def offset(delta: Int): Triangle = Triangle(v1 + delta, v2 + delta, v3 + delta)

  def nonZeroArea(points: NArray[Vector3]):Boolean = Triangle.nonZeroArea(points(v1), points(v2), points(v3))

  override def toString: String = s"Triangle($v1, $v2, $v3)"
}
