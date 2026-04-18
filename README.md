# ShimmerX

Compose Multiplatform sample app with a reusable **shimmer** loading animation for **Android**, **iOS**, **Desktop (JVM)**, and **Web** (Kotlin/JS & Wasm).

## Project layout

- [`composeApp/src/commonMain`](./composeApp/src/commonMain/kotlin) — shared UI and shimmer (`com.maulik.shimmerx.shimmer`).
- Platform-specific entry points: `androidMain`, `iosMain`, `jvmMain`, `jsMain`, `wasmJsMain`, `webMain`.

## How to use the shimmer API

### 1. Wrap your screen with `ShimmerAppTheme`

Place it **inside** `MaterialTheme { }` so shimmer light/dark matches your `ColorScheme` (surfaces and placeholders stay consistent on every platform).

```kotlin
MaterialTheme {
    ShimmerAppTheme(
        appearance = ShimmerAppearance(
            light = ShimmerDefaults.Light,
            dark = ShimmerDefaults.Dark,
        ),
    ) {
        // Your content
    }
}
```

- **`ShimmerDefaults.Light` / `Dark`** — gradient presets and timing.
- **`ShimmerAppearance`** — separate configs for light and dark UI.

### 2. Add a base color, then `shimmerx`

Typical pattern: `background(placeholderColor, shape)` then `Modifier.shimmerx(shape = shape)`.

```kotlin
val base = MaterialTheme.colorScheme.surfaceContainerHigh

Box(
    Modifier
        .size(48.dp)
        .background(base, CircleShape)
        .shimmerx(shape = CircleShape),
)
```

Use **`shape`** that matches the background (e.g. `RoundedCornerShape(8.dp)` for rounded rects).

### 3. Optional: `ShimmerCircle`, `ShimmerBlock`, `ShimmerTextLines`

Helpers in `ShimmerPlaceholders.kt` wrap `ShimmerBox` + `shimmerx` for common skeletons:

```kotlin
ShimmerCircle(size = 52.dp, baseColor = base)
ShimmerBlock(
    modifier = Modifier.fillMaxWidth().height(12.dp),
    cornerRadius = 4.dp,
    baseColor = base,
)
ShimmerTextLines(lineCount = 3, baseColor = base, lastLineFraction = 0.6f)
```

### 4. Per-widget overrides

On `Modifier.shimmerx`:

- **`themeOverride`** — custom `ShimmerTheme` (colors, duration, angle).
- **`colorVariant`** — `ShimmerColorVariant.Default`, `Dark`, `Ocean`, `Sunset`, `Emerald`.
- **`colors`** — explicit list of gradient stops.
- **`clip`** — clipping (often combined with `ShimmerConfig` from theme).

### 5. Shared animation (optional)

`ShimmerAppTheme` provides a single `ShimmerState` so all `shimmerx` modifiers can share the same animation. Set **`useProviderProgress = false`** on a modifier to run its own independent animation.

---

## Build and run

### Android

```shell
./gradlew :composeApp:assembleDebug
```

### Desktop (JVM)

```shell
./gradlew :composeApp:run
```

### Web (Wasm — recommended)

```shell
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

### Web (JS)

```shell
./gradlew :composeApp:jsBrowserDevelopmentRun
```

### iOS

Open [`iosApp`](./iosApp) in Xcode and run, or use the IDE run configuration.

---

## Learn more

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)
