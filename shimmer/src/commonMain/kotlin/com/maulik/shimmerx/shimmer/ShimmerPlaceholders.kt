package com.maulik.shimmerx.shimmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerCircle(
    size: Dp,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    baseColor: Color = Color(0xFFE2E8F0),
    themeOverride: ShimmerTheme? = null,
) {
    ShimmerBox(
        modifier = modifier.size(size),
        visible = visible,
        shape = CircleShape,
        baseColor = baseColor,
        themeOverride = themeOverride,
    )
}

@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    baseColor: Color = Color(0xFFE2E8F0),
    cornerRadius: Dp = 12.dp,
    themeOverride: ShimmerTheme? = null,
) {
    ShimmerBox(
        modifier = modifier,
        visible = visible,
        shape = RoundedCornerShape(cornerRadius),
        baseColor = baseColor,
        themeOverride = themeOverride,
    )
}

@Composable
fun ShimmerTextLines(
    lineCount: Int,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    lineHeight: Dp = 14.dp,
    lineSpacing: Dp = 8.dp,
    lastLineFraction: Float = 0.65f,
    baseColor: Color = Color(0xFFE2E8F0),
    themeOverride: ShimmerTheme? = null,
) {
    require(lineCount > 0) { "ShimmerTextLines lineCount must be > 0." }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(lineSpacing),
    ) {
        repeat(lineCount) { index ->
            val widthModifier = if (index == lineCount - 1) {
                Modifier.fillMaxWidth(lastLineFraction.coerceIn(0.1f, 1f))
            } else {
                Modifier.fillMaxWidth()
            }

            ShimmerBlock(
                modifier = widthModifier.height(lineHeight),
                visible = visible,
                cornerRadius = 4.dp,
                baseColor = baseColor,
                themeOverride = themeOverride,
            )
        }
    }
}
