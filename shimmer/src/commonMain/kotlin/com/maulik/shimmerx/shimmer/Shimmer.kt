package com.maulik.shimmerx.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.composed
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin

private fun degreesToRadians(degrees: Double): Double = degrees * PI / 180.0

/** How many identical color runs along the brush; translation by one segment length matches the next tile (no seam). */
private const val SHIMMER_SPATIAL_PERIODS = 3

private fun spatiallyTiledShimmerColors(base: List<Color>): List<Color> =
    List(SHIMMER_SPATIAL_PERIODS) { base }.flatten()

/** WCAG relative luminance (sRGB). */
private fun Color.relativeLuminance(): Float {
    fun linearize(c: Float): Float {
        val x = c.coerceIn(0f, 1f)
        return if (x <= 0.03928f) x / 12.92f else ((x + 0.055f) / 1.055f).pow(2.4f)
    }
    val r = linearize(red)
    val g = linearize(green)
    val b = linearize(blue)
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}

/** Matches shimmer light/dark to the same [ColorScheme] used for placeholder surfaces (e.g. [ColorScheme.surface]). */
private fun ColorScheme.shimmerMatchesDarkMaterialSurfaces(): Boolean =
    surface.relativeLuminance() < 0.5f

@Immutable
data class ShimmerTheme(
    val colors: List<Color>,
    val durationMillis: Int = 1_500,
    val angleInDegrees: Float = 20f,
) {
    init {
        require(colors.size >= 2) { "ShimmerTheme requires at least 2 colors." }
        require(durationMillis > 0) { "ShimmerTheme durationMillis must be > 0." }
    }
}

@Immutable
enum class ShimmerColorVariant {
    Default,
    Dark,
    Ocean,
    Sunset,
    Emerald,
}

object ShimmerColorVariants {
    fun colorsOf(variant: ShimmerColorVariant): List<Color> = when (variant) {
        ShimmerColorVariant.Default -> listOf(
            Color(0xFFE2E8F0),
            Color(0xFFF8FAFC),
            Color(0xFFE2E8F0),
        )
        ShimmerColorVariant.Dark -> listOf(
            Color(0xFF1E293B),
            Color(0xFF334155),
            Color(0xFF1E293B),
        )
        ShimmerColorVariant.Ocean -> listOf(
            Color(0xFF0F172A),
            Color(0xFF0EA5E9),
            Color(0xFF22D3EE),
            Color(0xFF0F172A),
        )
        ShimmerColorVariant.Sunset -> listOf(
            Color(0xFF7C2D12),
            Color(0xFFF97316),
            Color(0xFFFDE68A),
            Color(0xFF7C2D12),
        )
        ShimmerColorVariant.Emerald -> listOf(
            Color(0xFF064E3B),
            Color(0xFF10B981),
            Color(0xFF6EE7B7),
            Color(0xFF064E3B),
        )
    }
}

object ShimmerThemes {
    val Default = ShimmerTheme(
        colors = ShimmerColorVariants.colorsOf(ShimmerColorVariant.Default),
        durationMillis = 1_500,
        angleInDegrees = 20f,
    )

    val Dark = ShimmerTheme(
        colors = ShimmerColorVariants.colorsOf(ShimmerColorVariant.Dark),
        durationMillis = 1_300,
        angleInDegrees = 18f,
    )
}

@Immutable
data class ShimmerConfig(
    val theme: ShimmerTheme = ShimmerThemes.Default,
    val colorVariant: ShimmerColorVariant? = null,
    val angleInDegrees: Float? = null,
    val useProviderProgress: Boolean = true,
    val clip: Boolean = false,
)

@Immutable
data class ShimmerAppearance(
    val light: ShimmerConfig = ShimmerDefaults.Light,
    val dark: ShimmerConfig = ShimmerDefaults.Dark,
)

object ShimmerDefaults {
    val Light = ShimmerConfig(
        theme = ShimmerThemes.Default,
        colorVariant = null,
        angleInDegrees = null,
        clip = true,
    )

    val Dark = ShimmerConfig(
        theme = ShimmerThemes.Dark,
        colorVariant = null,
        angleInDegrees = null,
        clip = true,
    )
}

@Stable
class ShimmerState internal constructor(
    val theme: ShimmerTheme,
    internal val progress: Float,
)

private val LocalShimmerState = compositionLocalOf<ShimmerState?> { null }
private val LocalShimmerConfig = compositionLocalOf { ShimmerConfig() }

/**
 * Forward-only linear sweep 0→1 each cycle. The draw path uses a **spatially tiled** gradient so
 * shifting by exactly one period realigns the pattern — no jump at [RepeatMode.Restart].
 */
@Composable
private fun rememberSeamlessShimmerProgress(durationMillis: Int, enabled: Boolean): Float {
    val transition = rememberInfiniteTransition(label = "shimmer_phase")
    val animated by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis.coerceAtLeast(1), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_progress",
    )
    return if (enabled) animated else 0f
}

@Composable
fun rememberShimmerState(
    theme: ShimmerTheme = ShimmerThemes.Default,
    enabled: Boolean = true,
): ShimmerState {
    val progress = rememberSeamlessShimmerProgress(theme.durationMillis, enabled)
    return remember(theme, progress) { ShimmerState(theme = theme, progress = progress) }
}

@Composable
fun ShimmerProvider(
    state: ShimmerState = rememberShimmerState(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalShimmerState provides state, content = content)
}

@Composable
fun ShimmerAppTheme(
    config: ShimmerConfig = ShimmerConfig(),
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit,
) {
    ShimmerAppTheme(
        appearance = ShimmerAppearance(light = config, dark = config),
        darkTheme = darkTheme,
        content = content,
    )
}

@Composable
fun ShimmerAppTheme(
    appearance: ShimmerAppearance,
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val isDark = darkTheme ?: MaterialTheme.colorScheme.shimmerMatchesDarkMaterialSurfaces()
    val config = if (isDark) appearance.dark else appearance.light
    val shimmerState = rememberShimmerState(theme = config.theme)
    CompositionLocalProvider(
        LocalShimmerConfig provides config,
        LocalShimmerState provides shimmerState,
        content = content,
    )
}

fun Modifier.shimmer(
    visible: Boolean,
    shape: Shape,
    themeOverride: ShimmerTheme? = null,
): Modifier = shimmerx(
    visible = visible,
    shape = shape,
    themeOverride = themeOverride,
    clip = true,
)

/**
 * Shimmer overlay for loading placeholders. Place inside [ShimmerAppTheme] (or [ShimmerProvider]) so
 * timing and theme match your app.
 */
fun Modifier.shimmerx(
    visible: Boolean = true,
    shape: Shape = RectangleShape,
    themeOverride: ShimmerTheme? = null,
    colorVariant: ShimmerColorVariant? = null,
    colors: List<Color>? = null,
    angleInDegrees: Float? = null,
    useProviderProgress: Boolean = true,
    clip: Boolean = false,
): Modifier = composed {
    val appConfig = LocalShimmerConfig.current
    val shimmerState = LocalShimmerState.current
    val resolvedUseProvider = useProviderProgress && appConfig.useProviderProgress
    val resolvedVariant = colorVariant ?: appConfig.colorVariant
    val resolvedAngle = angleInDegrees ?: appConfig.angleInDegrees
    val baseTheme = themeOverride ?: shimmerState?.theme ?: appConfig.theme
    val theme = if (colors != null || angleInDegrees != null) {
        ShimmerTheme(
            colors = colors ?: if (resolvedVariant != null) {
                ShimmerColorVariants.colorsOf(resolvedVariant)
            } else {
                baseTheme.colors
            },
            durationMillis = baseTheme.durationMillis,
            angleInDegrees = resolvedAngle ?: baseTheme.angleInDegrees,
        )
    } else if (resolvedVariant != null || resolvedAngle != null) {
        ShimmerTheme(
            colors = if (resolvedVariant != null) {
                ShimmerColorVariants.colorsOf(resolvedVariant)
            } else {
                baseTheme.colors
            },
            durationMillis = baseTheme.durationMillis,
            angleInDegrees = resolvedAngle ?: baseTheme.angleInDegrees,
        )
    } else {
        baseTheme
    }
    val needsLocalProgress = visible && (!resolvedUseProvider || shimmerState == null)
    val localProgress = rememberSeamlessShimmerProgress(theme.durationMillis, needsLocalProgress)

    val progress = when {
        !visible -> 0f
        resolvedUseProvider && shimmerState != null -> shimmerState.progress
        else -> localProgress
    }

    val shimmerModifier = Modifier.drawWithContent {
        val sizeHypot = hypot(size.width, size.height).coerceAtLeast(1f)
        val periodLength = sizeHypot * 2f
        val gradientSpan = periodLength * SHIMMER_SPATIAL_PERIODS
        val halfBand = gradientSpan / 2f
        val angleRadians = degreesToRadians(theme.angleInDegrees.toDouble())
        val directionX = cos(angleRadians).toFloat()
        val directionY = sin(angleRadians).toFloat()

        val offset = (progress * periodLength) - (periodLength / 2f)
        val center = Offset(
            x = (size.width / 2f) + (directionX * offset),
            y = (size.height / 2f) + (directionY * offset),
        )
        val start = Offset(
            x = center.x - (directionX * halfBand),
            y = center.y - (directionY * halfBand),
        )
        val end = Offset(
            x = center.x + (directionX * halfBand),
            y = center.y + (directionY * halfBand),
        )
        val brush = Brush.linearGradient(
            colors = spatiallyTiledShimmerColors(applyShimmerGradientWorkaround(theme.colors)),
            start = start,
            end = end,
        )

        if (!visible) {
            drawContent()
            return@drawWithContent
        }

        drawContent()
        drawRect(brush = brush)
    }

    val resolvedClip = clip || appConfig.clip
    if (resolvedClip) {
        this.then(Modifier.clip(shape).then(shimmerModifier))
    } else {
        this.then(shimmerModifier)
    }
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    shape: Shape,
    baseColor: Color = Color(0xFFE2E8F0),
    themeOverride: ShimmerTheme? = null,
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .clip(shape)
            .background(baseColor)
            .shimmerx(
                visible = visible,
                shape = shape,
                themeOverride = themeOverride,
                clip = true,
            )
    )
}
