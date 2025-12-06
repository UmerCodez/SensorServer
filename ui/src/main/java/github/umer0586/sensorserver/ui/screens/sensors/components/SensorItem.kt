/*
 *     This file is a part of SensorServer (https://www.github.com/UmerCodez/SensorServer)
 *     Copyright (C) 2025 Umer Farooq (umerfarooq2383@gmail.com)
 *
 *     SensorServer is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     SensorServer is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with SensorServer. If not, see <https://www.gnu.org/licenses/>.
 *
 */
package github.umer0586.sensorserver.ui.screens.sensors.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.umer0586.sensorserver.data.model.DeviceSensor
import github.umer0586.sensorserver.ui.theme.SensorServerTheme

@Composable
fun SensorItem(
    sensor: DeviceSensor,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { showDetails = !showDetails }
    ) {
        ListItem(
            headlineContent = { Text(text = sensor.name) },
            supportingContent = {
                BasicText(
                    text = "type = ${sensor.stringType}",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 8.sp,
                        maxFontSize = 12.sp,
                    )
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )
        AnimatedVisibility(showDetails) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("maxRange = ${sensor.maximumRange}")
                Text("minDelay = ${sensor.minDelay}")
                Text("maxDelay = ${sensor.maxDelay}")
                Text("reportingMode = ${sensor.reportingModeString}")
                Text("wakeUpSensor = ${sensor.isWakeUpSensor}")
                Text("power = ${sensor.power}")
                Text("resolution = ${sensor.resolution}")
                Text("vendor = ${sensor.vendor}")

            }
        }
    }
}

@Preview
@Composable
private fun SensorItemPreview() {
    SensorServerTheme{
        Surface {
            SensorItem(
                sensor = DeviceSensor(
                    name = "Accelerometer",
                    stringType = "android.sensor.accelerometer",
                    maximumRange = 10.0f,
                    minDelay = 1000,
                    maxDelay = 1000,
                    reportingMode = 1,
                    isWakeUpSensor = true,
                    power = 2f,
                    resolution = 10.0f,
                    vendor = "Sensor Vendor",
                    type = 1,
                ),
            )
        }
    }
}