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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import github.umer0586.sensorserver.data.repository.SettingsRepository
import github.umer0586.sensorserver.data.service.websocket.WebsocketServerService
import github.umer0586.sensorserver.data.service.websocket.WebsocketServerServiceBindHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ServerScreenViewModel @Inject constructor(
    private val websocketServerServiceBindHelper: WebsocketServerServiceBindHelper,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServerScreenState())
    val state = _state.asStateFlow()

    private var webSocketServerService: WebsocketServerService? = null

    init {


        viewModelScope.launch {
            settingsRepository.settings.collect { setting ->
                _state.update {
                    it.copy(settings = setting)
                }
            }
        }

        websocketServerServiceBindHelper.onConnected { service ->
            webSocketServerService = service
            viewModelScope.launch {
                service.websocketServerState.collect { websocketServerState ->
                    _state.update {
                        it.copy(websocketServerState = websocketServerState)
                    }
                }
            }

            viewModelScope.launch {
                service.connectedClients.collect { connectedClients ->
                    _state.update {
                        it.copy(connectionsCount = connectedClients.size)
                    }
                }
            }
        }


     websocketServerServiceBindHelper.bind()
    }

    fun onEvent(event: ServerScreenEvent) {
        when(event) {
            ServerScreenEvent.OnStartClick -> {
                webSocketServerService?.startServer()
            }
            ServerScreenEvent.OnStopClick -> {
                webSocketServerService?.stopServer()
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        websocketServerServiceBindHelper.unbind()
    }

}