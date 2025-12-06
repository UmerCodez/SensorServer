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
package github.umer0586.sensorserver.data.service.websocket

import android.view.MotionEvent
import github.umer0586.sensorserver.data.model.websocket.ServiceRegistrationState
import github.umer0586.sensorserver.data.model.websocket.WebsocketClient
import github.umer0586.sensorserver.data.model.websocket.WebsocketServerState
import kotlinx.coroutines.flow.SharedFlow

interface WebsocketServerService {

    val websocketServerState: SharedFlow<WebsocketServerState>
    val connectedClients: SharedFlow<List<WebsocketClient>>
    val serviceRegistrationState: SharedFlow<ServiceRegistrationState>


    fun startServer()
    fun stopServer()
    fun closeConnection(websocketClient: WebsocketClient)
    fun sendMotionEvent(motionEvent: MotionEvent)
}