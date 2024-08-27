/*
 * Copyright (C) 2024 Rahmouni Neïl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.rahmouni.neil.counters.core.shapes

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Matrix
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.rectangle
import androidx.graphics.shapes.star
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class ShapeItem(
    val name: String,
    val shapegen: () -> RoundedPolygon,
    val shapeDetails: String = "",
    val usesSides: Boolean = true,
    val usesInnerRatio: Boolean = true,
    val usesRoundness: Boolean = true,
    val usesInnerParameters: Boolean = true,
)

class ShapeParameters(
    sides: Int = 5,
    innerRadius: Float = 0.5f,
    roundness: Float = 0f,
    smooth: Float = 0f,
    innerRoundness: Float = roundness,
    innerSmooth: Float = smooth,
    rotation: Float = 0f,
    shapeId: ShapeId = ShapeId.Polygon,
) {
    private val sides = mutableFloatStateOf(sides.toFloat())
    private val innerRadius = mutableFloatStateOf(innerRadius)
    private val roundness = mutableFloatStateOf(roundness)
    private val smooth = mutableFloatStateOf(smooth)
    private val innerRoundness = mutableFloatStateOf(innerRoundness)
    private val innerSmooth = mutableFloatStateOf(innerSmooth)
    private val rotation = mutableFloatStateOf(rotation)

    private var shapeIx by mutableIntStateOf(shapeId.ordinal)

    enum class ShapeId {
        Star,
        Polygon,
        Triangle,
        Blob,
        CornerSE,
    }

    // Primitive shapes we can draw (so far)
    private val shapes = listOf(
        ShapeItem(
            name = "Star",
            shapegen = {
                RoundedPolygon.star(
                    numVerticesPerRadius = this.sides.floatValue.roundToInt(),
                    innerRadius = this.innerRadius.floatValue,
                    rounding = CornerRounding(this.roundness.floatValue, this.smooth.floatValue),
                    innerRounding = CornerRounding(
                        this.innerRoundness.floatValue,
                        this.innerSmooth.floatValue,
                    ),
                )
            },
        ),
        ShapeItem(
            name = "Polygon",
            shapegen = {
                RoundedPolygon(
                    numVertices = this.sides.floatValue.roundToInt(),
                    rounding = CornerRounding(this.roundness.floatValue, this.smooth.floatValue),
                )
            },
            usesInnerRatio = false,
            usesInnerParameters = false,
        ),
        ShapeItem(
            name = "Triangle",
            shapegen = {
                val points = floatArrayOf(
                    radialToCartesian(1f, 270f.toRadians()).x,
                    radialToCartesian(1f, 270f.toRadians()).y,
                    radialToCartesian(1f, 30f.toRadians()).x,
                    radialToCartesian(1f, 30f.toRadians()).y,
                    radialToCartesian(this.innerRadius.floatValue, 90f.toRadians()).x,
                    radialToCartesian(this.innerRadius.floatValue, 90f.toRadians()).y,
                    radialToCartesian(1f, 150f.toRadians()).x,
                    radialToCartesian(1f, 150f.toRadians()).y,
                )
                RoundedPolygon(
                    points,
                    CornerRounding(this.roundness.floatValue, this.smooth.floatValue),
                    centerX = 0f,
                    centerY = 0f,
                )
            },
            usesSides = false,
            usesInnerParameters = false,
        ),
        ShapeItem(
            name = "Blob",
            shapegen = {
                val sx = this.innerRadius.floatValue.coerceAtLeast(0.1f)
                val sy = this.roundness.floatValue.coerceAtLeast(0.1f)
                RoundedPolygon(
                    vertices = floatArrayOf(
                        -sx,
                        -sy,
                        sx,
                        -sy,
                        sx,
                        sy,
                        -sx,
                        sy,
                    ),
                    rounding = CornerRounding(min(sx, sy), this.smooth.floatValue),
                    centerX = 0f,
                    centerY = 0f,
                )
            },
            usesSides = false,
            usesInnerParameters = false,
        ),
        ShapeItem(
            name = "CornerSE",
            shapegen = {
                RoundedPolygon(
                    squarePoints(),
                    perVertexRounding = listOf(
                        CornerRounding(this.roundness.floatValue, this.smooth.floatValue),
                        CornerRounding(1f),
                        CornerRounding(1f),
                        CornerRounding(1f),
                    ),
                    centerX = 0f,
                    centerY = 0f,
                )
            },
            usesSides = false,
            usesInnerRatio = false,
            usesInnerParameters = false,
        ),
        ShapeItem(
            name = "Circle",
            shapegen = {
                RoundedPolygon.circle(this.sides.floatValue.roundToInt())
            },
            usesSides = true,
            usesInnerRatio = false,
            usesInnerParameters = false,
        ),
        ShapeItem(
            name = "Rectangle",
            shapegen = {
                RoundedPolygon.rectangle(
                    width = 4f,
                    height = 2f,
                    rounding = CornerRounding(this.roundness.floatValue, this.smooth.floatValue),
                )
            },
            usesSides = false,
            usesInnerRatio = false,
            usesInnerParameters = false,
        ),
    )

    private fun selectedShape() = derivedStateOf { shapes[shapeIx] }

    fun genShape(addedRotation: Float = 0f, autoSize: Boolean = true) =
        selectedShape().value.shapegen().let { poly ->
            poly.transformed(
                Matrix().apply {
                    if (autoSize) {
                        val bounds = poly.getBounds()
                        // Move the center to the origin.
                        translate(
                            x = -(bounds.left + bounds.right) / 2,
                            y = -(bounds.top + bounds.bottom) / 2,
                        )

                        // Scale to the [-1, 1] range
                        val scale = 2f / max(bounds.width, bounds.height)
                        scale(x = scale, y = scale)
                    }
                    // Apply the needed rotation
                    rotateZ(rotation.floatValue + addedRotation)
                },
            )
        }
}

private fun squarePoints() = floatArrayOf(1f, 1f, -1f, 1f, -1f, -1f, 1f, -1f)
