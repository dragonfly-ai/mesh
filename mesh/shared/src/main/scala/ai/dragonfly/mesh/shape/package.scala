package ai.dragonfly.mesh

import ai.dragonfly.math.vector.*
import Vec.*
import narr.*

package object shape {

  def addQuad(a: Int, b: Int, c: Int, d: Int, triangles: NArray[Triangle], t: Int): Int = if (t < triangles.length) {
    triangles(t) = Triangle(a, b, c)
    triangles(t + 1) = Triangle(a, d, b)
    t + 2
  } else t

  inline def validateVertex(v:Int, vertices:NArray[Vec[3]]): Boolean = vertices.length > v

  inline def addQuad(a: Int, b: Int, c: Int, d: Int, triangles: NArray[Triangle], t: Int, vertices:NArray[Vec[3]]): Int = {

    if (
      t < triangles.length &&
      validateVertex(a, vertices) &&
      validateVertex(b, vertices) &&
      validateVertex(c, vertices)
    ) {
      triangles(t) = Triangle(a, b, c)
      t + 1 + (if (validateVertex(d, vertices)) {
        triangles(t + 1) = Triangle(a, d, b)
        1
      } else 0)
    } else t
  }

}
