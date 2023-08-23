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
import ai.dragonfly.math.vector.*
import ai.dragonfly.math.Constant.π
import ai.dragonfly.math.{cubeInPlace, squareInPlace}
import ai.dragonfly.mesh.*

import scala.scalajs.js.annotation.JSExportTopLevel

object Cylinder {
  @JSExportTopLevel("Cylinder")
  def apply(angularSegments: Int = 36, sideSegments: Int = 1, baseSegments: Int = 1, capSegments: Int = 1, radius: Double = 1.0, height: Double = 1.0): Mesh = {
    Drum.apply(angularSegments, sideSegments, baseSegments, capSegments, radius, radius, height)
  }
}
