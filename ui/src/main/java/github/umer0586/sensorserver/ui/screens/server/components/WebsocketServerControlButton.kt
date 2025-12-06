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
package github.umer0586.sensorserver.ui.screens.server.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import github.umer0586.sensorserver.data.model.websocket.WebsocketServerState
import github.umer0586.sensorserver.ui.theme.SensorServerTheme

@Composable
fun WebsocketServerControlButton(
    modifier: Modifier = Modifier,
    websocketServerState: WebsocketServerState,
    onStartClick: () -> Unit = {},
    onStopClick: () -> Unit = {}
) {
    TextButton(
        modifier = modifier.aspectRatio(1f),
        onClick = {
            when(websocketServerState){
                is WebsocketServerState.Running -> onStopClick()
                else -> onStartClick()
            }
        },
        colors = ButtonDefaults.textButtonColors(
            containerColor = when(websocketServerState){
                is WebsocketServerState.Running -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            },
            contentColor = when(websocketServerState){
                is WebsocketServerState.Running -> MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.colorScheme.onPrimaryContainer
            }
        )
    ) {

        val textFontSize = 25.sp

        if(websocketServerState is WebsocketServerState.Running)
            Text(
                fontSize = textFontSize,
                text = "STOP"
            )
        else
            Text(
                fontSize = textFontSize,
                text = "START"
            )
    }
}

@Preview
@Composable
private fun Preview() {
    SensorServerTheme {
        WebsocketServerControlButton(websocketServerState = WebsocketServerState.Stopped)
    }
}