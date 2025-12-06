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
package github.umer0586.sensorserver.ui.screens.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import github.umer0586.sensorserver.data.service.http.HttpServerService
import github.umer0586.sensorserver.data.service.http.HttpServerServiceBindHelper
import github.umer0586.sensorserver.data.service.websocket.WebsocketServerServiceBindHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val websocketServerServiceBindHelper: WebsocketServerServiceBindHelper,
    private val httpServerServiceBindHelper: HttpServerServiceBindHelper
) : ViewModel(){

    private val _state = MutableStateFlow(NavigationScreenState())
    val state = _state.asStateFlow()

    private var httpServerService: HttpServerService? = null

    init {

        websocketServerServiceBindHelper.onConnected { websocketServerService ->

            viewModelScope.launch {
                websocketServerService.connectedClients.collect { connectedClients ->
                    _state.update {
                        it.copy(connectionsCount = connectedClients.size)
                    }
                }
            }
        }

        httpServerServiceBindHelper.onConnected { httpServerService ->
            this.httpServerService = httpServerService

            viewModelScope.launch {
                httpServerService.httpServerState.collect { httpServerState ->
                    _state.update {
                        it.copy(httpServerState = httpServerState)
                    }
                }
            }
        }


        websocketServerServiceBindHelper.bind()
        httpServerServiceBindHelper.bind()
    }

    fun onEvent(event: NavigationScreenEvent) {
        when(event){
            NavigationScreenEvent.OnStartHttpServerClick -> httpServerService?.startServer()
            NavigationScreenEvent.OnStopHttpServerClick -> httpServerService?.stopServer()
        }
    }


    override fun onCleared() {
        super.onCleared()
        websocketServerServiceBindHelper.unbind()
        httpServerServiceBindHelper.unBind()

    }

}