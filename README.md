<p align="center">
  <img src="asset/ShimmerX.png" alt="ShimmerX" width="480" />
</p>

<h1 align="center">ShimmerX</h1>

<p align="center"><strong>Smooth, theme-aware loading shimmer for Compose Multiplatform — one API across Android, iOS, Desktop, and Web.</strong></p>

---

<p align="center">
  <a href="https://raw.githubusercontent.com/maulikdadhaniya/ShimmerX/main/asset/ShimmerX.mp4">
    🎥 Watch Demo
  </a>
</p>

---
## Setup

### From Maven Central

Add the published library (replace the version if you use a different release):

**Kotlin Multiplatform** — in your shared source set, e.g. `commonMain`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.maulikdadhaniya:shimmerx:<LATEST_VERSION>")
        }
    }
}
```

**Android-only** module:

```kotlin
dependencies {
    implementation("io.github.maulikdadhaniya:shimmerx:<LATEST_VERSION>")
}
```

> Kotlin Multiplatform also publishes per-target Maven modules (e.g. `shimmerx-iosarm64`). You still declare **only** the line above; Gradle picks the right artifact per platform.

### From this repository (sample + library sources)

Clone the repo and open it in Android Studio or IntelliJ. The **library** lives in the **`shimmerX`** module; **`composeApp`** is a demo app.

---

## How to use?

### 1. Wrap your UI with `ShimmerAppTheme`

Place it **inside** `MaterialTheme { }` so shimmer matches your `ColorScheme`.

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

### 2. Base color + `shimmerx` modifier

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

Use a **`shape`** that matches the background (e.g. `RoundedCornerShape(8.dp)` for rounded rectangles).

### 3. Optional: `ShimmerCircle`, `ShimmerBlock`, `ShimmerTextLines`

Helpers for common skeletons:

```kotlin
ShimmerCircle(size = 52.dp, baseColor = base)
ShimmerBlock(
    modifier = Modifier.fillMaxWidth().height(12.dp),
    cornerRadius = 4.dp,
    baseColor = base,
)
ShimmerTextLines(lineCount = 3, baseColor = base, lastLineFraction = 0.6f)
```

### 4. Overrides and shared animation

On `Modifier.shimmerx`:

- **`themeOverride`** — custom `ShimmerTheme` (colors, duration, angle).
- **`colorVariant`** — `ShimmerColorVariant.Default`, `Dark`, `Ocean`, `Sunset`, `Emerald`.
- **`colors`** — explicit gradient stops.
- **`clip`** — clipping with your shape.

`ShimmerAppTheme` shares one `ShimmerState` across placeholders. Use **`useProviderProgress = false`** on a modifier for an independent animation.

---
## Build and run the sample

| Platform | Command |
|----------|---------|
| Android | `./gradlew :composeApp:assembleDebug` |
| Desktop (JVM) | `./gradlew :composeApp:run` |
| Web (Wasm) | `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` |
| Web (JS) | `./gradlew :composeApp:jsBrowserDevelopmentRun` |
| iOS | Open `iosApp` in Xcode and run. |

---

## Learn more

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)

---

<p align="center">Made by <strong>❤️ Maulik Dadhaniya</strong></p>
