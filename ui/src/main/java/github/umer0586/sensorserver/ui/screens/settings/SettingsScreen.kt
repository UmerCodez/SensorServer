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
package github.umer0586.sensorserver.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import github.umer0586.sensorserver.ui.screens.settings.components.EditTextPref
import github.umer0586.sensorserver.ui.screens.settings.components.SwitchPref
import github.umer0586.sensorserver.ui.theme.SensorServerTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsScreenState,
    onEvent: (SettingsScreenEvent) -> Unit
) {

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            EditTextPref(
                value = state.setting.websocketPort.toString(),
                title = { Text("Websocket Port") },
                isError = { value ->
                    value.isEmpty() || value.toIntOrNull() == null || value.toInt() !in 1024..49151
                },
                errorText = "Range: 1024-49151",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                onUpdateClick = {

                    it.toInt().also { webSocketPortNo ->
                        if (state.setting.httpPort == webSocketPortNo) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Websocket and Http port cannot be same")
                            }
                        } else {
                            onEvent(SettingsScreenEvent.OnWebsocketPortChange(webSocketPortNo))
                        }
                    }


                }
            )
            EditTextPref(
                value = state.setting.httpPort.toString(),
                title = { Text("http Port") },
                isError = { value ->
                    value.isEmpty() || value.toIntOrNull() == null || value.toInt() !in 1024..49151
                },
                errorText = "Range: 1024-49151",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                onUpdateClick = onUpdateClick@{

                    it.toInt().also { httpPortNo ->
                        if (state.setting.websocketPort == httpPortNo) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Http and Websocket port cannot be same")
                            }
                        } else {
                            onEvent(SettingsScreenEvent.OnHttpPortChange(httpPortNo))
                        }
                    }

                }

            )
            EditTextPref(
                value = state.setting.samplingRate.toString(),
                title = { Text("Sampling Rate") },
                isError = { value ->
                    value.isEmpty() || value.toIntOrNull() == null || value.toInt() !in 0..200000
                },
                errorText = "Range: 0-200000",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                onUpdateClick = { onEvent(SettingsScreenEvent.OnSamplingRateChange(it.toInt())) }
            )
            SwitchPref(
                checked = state.setting.listenOnAllInterface,
                onCheckedChange = { onEvent(SettingsScreenEvent.OnListenOnAllInterfaceChange(it)) },
                title = { Text("Listen on 0.0.0.0") },
                summary = { Text("Listen on all interface") },
            )
            SwitchPref(
                checked = state.setting.useHotSpot,
                onCheckedChange = { onEvent(SettingsScreenEvent.OnUseHotSpotChange(it)) },
                title = { Text("HotSpot") },
                summary = { Text("Use device's Hotspot") },
            )
            SwitchPref(
                checked = state.setting.useLocalHost,
                onCheckedChange = { onEvent(SettingsScreenEvent.OnUseLocalHostChange(it)) },
                title = { Text("LocalHost") },
                summary = { Text("Use adb to connect over USB") },
            )
            SwitchPref(
                checked = state.setting.discoverable,
                onCheckedChange = { onEvent(SettingsScreenEvent.OnDiscoverableChange(it)) },
                title = { Text("Discoverable") },
                summary = { Text("Make Websocket server discoverable") },
            )

        }
    }

}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SensorServerTheme {
        SettingsScreen(
            state = SettingsScreenState(),
            onEvent = {}
        )
    }
}