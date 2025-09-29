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

object sRGB {

  opaque type ARGB32 = Int

  object ARGB32 {

    def apply(ci:Int): ARGB32 = ci

    /**
     * Factory method to create a fully opaque ARGB instance from separate, specified red, green, blue components and
     * a default alpha value of 255.
     * Parameter values are derived from the least significant byte.  Integer values that range outside of [0-255] may
     * give unexpected results.  For values taken from user input, sensors, or otherwise uncertain sources, consider using
     * the factory method in the Color companion object.
     *
     * @see [[ai.dragonfly.color.ColorVectorSpace.argb]] for a method of constructing ARGB objects that validates inputs.
     * @param red   integer value from [0-255] representing the red component in RGB space.
     * @param green integer value from [0-255] representing the green component in RGB space.
     * @param blue  integer value from [0-255] representing the blue component in RGB space.
     * @return an instance of the ARGB case class.
     * @example {{{ val c = ARGB32(72,105,183) }}}
     */
    def apply(red: Int, green: Int, blue: Int): ARGB32 = apply(255, red, green, blue)

    /**
     * Factory method to create an ARGB instance from separate, specified red, green, blue, and alpha components.
     * Parameter values are derived from the least significant byte.  Integer values that range outside of [0-255] may
     * give unexpected results.  For values taken from user input, sensors, or otherwise uncertain sources, consider using
     * the factory method in the Color companion object.
     *
     * @see [[ai.dragonfly.color.ARGB.getIfValid]] for a method of constructing ARGB objects with input validation.
     * @param alpha integer value from [0-255] representing the alpha component in ARGB space.  Defaults to 255.
     * @param red   integer value from [0-255] representing the red component in RGB space.
     * @param green integer value from [0-255] representing the green component in RGB space.
     * @param blue  integer value from [0-255] representing the blue component in RGB space.
     * @return an instance of the ARGB case class.
     * @example {{{ val c = ARGB32(72,105,183) }}}
     */
    def apply(alpha: Int, red: Int, green: Int, blue: Int): ARGB32 = (alpha << 24) | (red << 16) | (green << 8) | blue
  }

  extension (argb:ARGB32) {

    /**
     * @return the alpha transparency component of this color in ARGB space with values ranging from: [0-255]
     */
    def alpha: Int = argb >> 24 & 0xff

    /**
     * @return the red component of this color in ARGB space with values ranging from: [0-255]
     */
    def red: Int = argb >> 16 & 0xff

    /**
     * @return the green component of this color in ARGB space with values ranging from: [0-255]
     */
    def green: Int = argb >> 8 & 0xff

    /**
     * @return the blue component of this color in ARGB space with values ranging from: [0-255]
     */
    def blue: Int = argb & 0xff


    /**
     * @return the red component of this color in ARGB space with values ranging from: [0.0f-1.0f]
     */
    def normalizedRed: Float = red / 255f

    /**
     * @return the green component of this color in ARGB space with values ranging from: [0.0f-1.0f]
     */
    def normalizedGreen: Float = green / 255f

    /**
     * @return the blue component of this color in ARGB space with values ranging from: [0.0f-1.0f]
     */
    def normalizedBlue: Float = blue / 255f

    /**
     * @return the alpha component of this color in ARGB space with values ranging from: [0.0f-1.0f]
     */
    def normalizedAlpha: Float = alpha / 255f
  }
}
