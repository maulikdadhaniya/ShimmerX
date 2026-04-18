package com.maulik.shimmerx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maulik.shimmerx.shimmer.ShimmerAppearance
import com.maulik.shimmerx.shimmer.ShimmerAppTheme
import com.maulik.shimmerx.shimmer.ShimmerBlock
import com.maulik.shimmerx.shimmer.ShimmerCircle
import com.maulik.shimmerx.shimmer.ShimmerDefaults
import com.maulik.shimmerx.shimmer.ShimmerTextLines

@Composable
@Preview
fun App() {
    MaterialTheme {
        var isLoading by remember { mutableStateOf(true) }

        ShimmerAppTheme(
            appearance = ShimmerAppearance(
                light = ShimmerDefaults.Light,
                dark = ShimmerDefaults.Dark,
            ),
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .safeContentPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = { isLoading = !isLoading },
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Text(if (isLoading) "Show content" else "Show shimmer")
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    repeat(4) {
                        ProfileListItem(isLoading = isLoading)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileListItem(isLoading: Boolean) {
    val cardShape = RoundedCornerShape(16.dp)
    val placeholderBase = MaterialTheme.colorScheme.surfaceContainerHigh

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, cardShape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (isLoading) {
            ShimmerCircle(
                size = 52.dp,
                baseColor = placeholderBase,
            )
        } else {
            Spacer(
                modifier = Modifier
                    .size(52.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                ShimmerTextLines(
                    lineCount = 2,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 12.dp,
                    lineSpacing = 8.dp,
                    lastLineFraction = 0.45f,
                    baseColor = placeholderBase,
                )
            } else {
                Text("Jane Cooper", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Senior Product Designer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (isLoading) {
            ShimmerBlock(
                modifier = Modifier
                    .width(72.dp)
                    .height(28.dp),
                cornerRadius = 14.dp,
                baseColor = placeholderBase,
            )
        } else {
            Button(onClick = {}, modifier = Modifier.height(34.dp)) {
                Text("View")
            }
        }
    }
}
