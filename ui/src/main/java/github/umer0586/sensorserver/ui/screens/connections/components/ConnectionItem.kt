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
package github.umer0586.sensorserver.ui.screens.connections.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import github.umer0586.sensorserver.data.model.DeviceSensor
import github.umer0586.sensorserver.data.model.websocket.GPSWebsocketClient
import github.umer0586.sensorserver.data.model.websocket.MultipleSensorWebsocketClient
import github.umer0586.sensorserver.data.model.websocket.SingleSensorWebsocketClient
import github.umer0586.sensorserver.data.model.websocket.TouchScreenWebsocketClient
import github.umer0586.sensorserver.data.model.websocket.WebsocketClient
import github.umer0586.sensorserver.ui.theme.SensorServerTheme

@Composable
fun ConnectionItem(
    websocketClient: WebsocketClient,
    modifier: Modifier = Modifier,
    onCloseClick: (WebsocketClient) -> Unit = {}
) {

    val sensorName = when(websocketClient){
        is SingleSensorWebsocketClient -> websocketClient.deviceSensor.name
        is TouchScreenWebsocketClient -> "Touchscreen"
        is GPSWebsocketClient -> "GPS"
        is MultipleSensorWebsocketClient -> websocketClient.deviceSensors.joinToString("\n") { it.name }
        else -> "Unknown"
    }

    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = websocketClient.toString()) },
        trailingContent = {
            TextButton(
                onClick = { onCloseClick(websocketClient) }
            ) {
                Text("Close")
            }
        },
        supportingContent = {
            Text(sensorName)
        }
    )

}

@Preview(showBackground = true)
@Composable
private fun ConnectionItemPreview() {

    SensorServerTheme {
        ConnectionItem(
            websocketClient = SingleSensorWebsocketClient(
                address = "192.168.1.1",
                port = 8080,
                deviceSensor = sensors[0]
            )
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun ConnectionItemMultiSensorPreview() {

    SensorServerTheme {
        ConnectionItem(
            websocketClient = MultipleSensorWebsocketClient(
                address = "192.168.1.1",
                port = 8080,
                deviceSensors = sensors
            )
        )
    }

}

private val sensors = listOf(
    DeviceSensor(
        name = "Accelerometer",
        stringType = "android.sensor.accelerometer",
        type = 1,
        maximumRange = 19.6133f,
        reportingMode = 0,
        maxDelay = 0,
        minDelay = 10000,
        vendor = "Google",
        power = 0.25f,
        resolution = 0.00239f,
        isWakeUpSensor = false
    ),
    DeviceSensor(
        name = "Gyroscope",
        stringType = "android.sensor.gyroscope",
        type = 4,
        maximumRange = 17.4533f,
        reportingMode = 0,
        maxDelay = 0,
        minDelay = 5000,
        vendor = "Google",
        power = 0.5f,
        resolution = 0.00053f,
        isWakeUpSensor = false
    ),
    DeviceSensor(
        name = "Light",
        stringType = "android.sensor.light",
        type = 5,
        maximumRange = 10000f,
        reportingMode = 1,
        maxDelay = 0,
        minDelay = 0,
        vendor = "Google",
        power = 0.1f,
        resolution = 1f,
        isWakeUpSensor = false
    ),
    DeviceSensor(
        name = "Magnetometer",
        stringType = "android.sensor.magnetic_field",
        type = 2,
        maximumRange = 1300f,
        reportingMode = 0,
        maxDelay = 0,
        minDelay = 10000,
        vendor = "Google",
        power = 0.5f,
        resolution = 0.0625f,
        isWakeUpSensor = false
    )
)