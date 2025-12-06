package github.umer0586.sensorserver.ui.screens.navigation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.umer0586.sensorserver.ui.theme.SensorServerTheme

@Composable
fun NavDrawerHeader(
    modifier: Modifier = Modifier,
    appVersion: String = "1.0.0"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val circle1Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
        val circle2Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.08f)
        val circle3Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
        // Decorative background circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = circle1Color,
                radius = 120.dp.toPx(),
                center = Offset(size.width * 0.2f, size.height * 0.3f)
            )
            drawCircle(
                color = circle2Color,
                radius = 160.dp.toPx(),
                center = Offset(size.width * 0.8f, size.height * 0.7f)
            )
            drawCircle(
                color = circle3Color,
                radius = 80.dp.toPx(),
                center = Offset(size.width * 0.9f, size.height * 0.2f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "Sensor Server",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Version badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Version v${appVersion}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtle tagline
            Text(
                text = "Stream Android's sensor data via WebSocket",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light
            )
        }
    }

}

@Preview
@Composable
private fun Preview() {
    SensorServerTheme {
        NavDrawerHeader()
    }
}