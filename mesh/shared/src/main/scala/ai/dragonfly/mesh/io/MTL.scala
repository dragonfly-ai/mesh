package ai.dragonfly.mesh.io

import ai.dragonfly.mesh.sRGB

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportTopLevel("MTL") @JSExportAll
object MTL {
  val gray:sRGB = sRGB(128, 128, 128)
  val default:MTL = MTL("metal", gray)
}

@JSExportAll
case class MTL(name: String, diffuse: sRGB, ambient: sRGB = gray, specular: Specular = Specular(), dissolve: Float = 1f) {
  def mtl: String = {
    val sb: StringBuilder = new StringBuilder()
    sb.append(s"newmtl $name\n")
    sb.append(s"Ka ${ambient.normalizedRed} ${ambient.normalizedGreen} ${ambient.normalizedBlue}\n")
    sb.append(s"Kd ${diffuse.normalizedRed} ${diffuse.normalizedGreen} ${diffuse.normalizedBlue}\n")
    sb.append(specular.mtl)
    sb.append(s"d $dissolve\n")
    sb.append("illum 1\n")
    sb.toString()
  }
}

case class Specular(color: sRGB = MTL.gray, exponent: Float = 10f) {
  def mtl: String = s"Ks ${color.normalizedRed} ${color.normalizedGreen} ${color.normalizedBlue}\nNs $exponent\n"
}
