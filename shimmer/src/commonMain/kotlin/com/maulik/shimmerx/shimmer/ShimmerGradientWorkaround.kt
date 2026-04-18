package com.maulik.shimmerx.shimmer

import androidx.compose.ui.graphics.Color

/**
 * JS/Wasm (Canvas) and JVM can interpolate [Brush.linearGradient] stops differently than Skia on
 * Android/iOS. Targets that need parity call [densifyShimmerGradient] on opaque stop lists.
 */
internal expect fun applyShimmerGradientWorkaround(colors: List<Color>): List<Color>

internal fun densifyShimmerGradient(colors: List<Color>, stepsPerSegment: Int = 16): List<Color> {
    if (colors.size < 2 || stepsPerSegment < 2) return colors
    val spans = colors.lastIndex
    val total = spans * stepsPerSegment + 1
    val out = ArrayList<Color>(total)
    for (k in 0 until total) {
        val u = k / (total - 1).coerceAtLeast(1).toFloat()
        out.add(sampleEvenStops(colors, u))
    }
    return out
}

private fun sampleEvenStops(colors: List<Color>, u: Float): Color {
    if (u <= 0f) return colors.first()
    if (u >= 1f) return colors.last()
    val m = colors.lastIndex
    val x = u * m
    val i = x.toInt().coerceIn(0, m - 1)
    val t = x - i
    return lerpRgb(colors[i], colors[i + 1], t)
}

private fun lerpRgb(a: Color, b: Color, t: Float): Color = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = a.alpha + (b.alpha - a.alpha) * t,
)
