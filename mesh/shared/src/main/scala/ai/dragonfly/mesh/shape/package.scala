package ai.dragonfly.mesh

import narr.*

package object shape {

  inline def addQuad(a: Int, b: Int, c: Int, d: Int, triangles: NArray[Triangle], t: Int): Int = if (t < triangles.length) {
    triangles(t) = Triangle(a, b, c)
    triangles(t + 1) = Triangle(a, d, b)
    t + 2
  } else t

}
