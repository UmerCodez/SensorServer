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
package github.umer0586.sensorserver.ui.screens.server

import android.Manifest
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import github.umer0586.sensorserver.data.model.websocket.WebsocketServerState
import github.umer0586.sensorserver.data.servers.websocket.WebsocketServerInfo
import github.umer0586.sensorserver.data.util.isHotSpotEnabled
import github.umer0586.sensorserver.ui.screens.server.components.AddressCard
import github.umer0586.sensorserver.ui.screens.server.components.WebsocketServerControlButton
import github.umer0586.sensorserver.ui.theme.SensorServerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.BindException
import java.net.UnknownHostException

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ServerScreen(
    viewModel: ServerScreenViewModel = hiltViewModel(),
    onError: ((String) -> Unit)? = null
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()
    val wifiManager = remember {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }


    ServerScreen(
        state = state,
        onError = onError,
        onEvent = onEvent@{ event ->

            if (event is ServerScreenEvent.OnStartClick) {
                notificationPermissionState?.launchPermissionRequest()
                val noOptionSelected =
                    !(state.settings.useLocalHost || state.settings.useHotSpot || state.settings.listenOnAllInterface)

                if (noOptionSelected && !wifiManager.isWifiEnabled) {
                    onError?.invoke("Please enable Wifi")
                    return@onEvent
                } else if (state.settings.useHotSpot && !wifiManager.isHotSpotEnabled()) {
                    onError?.invoke("Please enable hotspot")
                    return@onEvent
                }
            }

            viewModel.onEvent(event)

        }
    )

}

@Composable
fun ServerScreen(
    state: ServerScreenState,
    onEvent: (ServerScreenEvent) -> Unit,
    onError: ((String) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    var startButtonClick by remember { mutableStateOf(false) }

    LaunchedEffect(state.websocketServerState) {
        if (state.websocketServerState is WebsocketServerState.Error && startButtonClick) {
            val exception = state.websocketServerState.throwable
            when (exception) {
                is BindException -> onError?.invoke("Port No already in use")
                is UnknownHostException -> onError?.invoke("Unable to obtain IP")
                else -> onError?.invoke("Unable to Start server")
            }
        }
    }


    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AnimatedVisibility(state.websocketServerState is WebsocketServerState.Running) {
                when (state.websocketServerState) {
                    is WebsocketServerState.Running -> {
                        val serverInfo = state.websocketServerState.websocketServerInfo
                        AddressCard("ws://${serverInfo.ipAddress}:${serverInfo.port}")
                    }

                    else -> {}
                }

            }

            AnimatedVisibility(state.websocketServerState is WebsocketServerState.Running) {
                Spacer(Modifier.height(80.dp))
            }

            WebsocketServerControlButton(
                modifier = Modifier.size(150.dp),
                websocketServerState = state.websocketServerState,
                onStartClick = {
                    onEvent(ServerScreenEvent.OnStartClick)
                    startButtonClick = true

                    scope.launch {
                        delay(1000)
                        startButtonClick = false
                    }
                },
                onStopClick = {
                    onEvent(ServerScreenEvent.OnStopClick)
                }
            )

        }
    }

}

@Preview
@Composable
fun ServerScreenPreview() {
    SensorServerTheme {
        ServerScreen(
            state = ServerScreenState(
                websocketServerState = WebsocketServerState.Running(
                    WebsocketServerInfo(
                        "192.168.18.50",
                        8080
                    )
                )
            ),
            onEvent = {}
        )
    }
}