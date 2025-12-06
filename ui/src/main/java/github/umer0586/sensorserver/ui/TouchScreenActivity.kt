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
package github.umer0586.sensorserver.ui

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import github.umer0586.sensorserver.data.service.websocket.WebsocketServerService
import github.umer0586.sensorserver.data.service.websocket.WebsocketServerServiceBindHelper
import javax.inject.Inject

@AndroidEntryPoint
class TouchScreenActivity: ComponentActivity() {
    @Inject
    lateinit var websocketServerServiceBindHelper: WebsocketServerServiceBindHelper
    private var websocketServerService: WebsocketServerService? = null
    private val tag = TouchScreenActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        websocketServerServiceBindHelper.onConnected { service ->
            websocketServerService = service
        }
        enableEdgeToEdge()
        setContent {

        }
    }

    override fun onResume() {
        super.onResume()
        websocketServerServiceBindHelper.bind()
    }

    override fun onPause() {
        super.onPause()
        websocketServerServiceBindHelper.unbind()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            websocketServerService?.sendMotionEvent(it)
        }
        return super.onTouchEvent(event)
    }

}