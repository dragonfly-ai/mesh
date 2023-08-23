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

package ai.dragonfly.mesh.io

import ai.dragonfly.mesh.sRGB.*

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportTopLevel("MTL") @JSExportAll
object MTL {
  val gray:ARGB32 = ARGB32(128, 128, 128)
  val default:MTL = MTL("metal", gray)

  def writeMTL(material: MTL, out: java.io.OutputStream): Unit = out.write(material.mtl.getBytes)
}

@JSExportAll
case class MTL(name: String, diffuse: ARGB32, ambient: ARGB32 = gray, specular: Specular = Specular(), dissolve: Float = 1f) {
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

case class Specular(color: ARGB32 = MTL.gray, exponent: Float = 10f) {
  def mtl: String = s"Ks ${color.normalizedRed} ${color.normalizedGreen} ${color.normalizedBlue}\nNs $exponent\n"
}
