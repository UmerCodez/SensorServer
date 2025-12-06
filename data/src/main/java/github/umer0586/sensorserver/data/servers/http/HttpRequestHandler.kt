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
package github.umer0586.sensorserver.data.servers.http

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.yanzhenjie.andserver.annotation.GetMapping
import com.yanzhenjie.andserver.annotation.RestController
import com.yanzhenjie.andserver.http.HttpResponse
import github.umer0586.sensorserver.data.repository.SettingsRepositoryImp
import github.umer0586.sensorserver.data.util.JsonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@RestController
class RequestController {

    @GetMapping("/wsport")
    fun getWebsocketPortNo(context : Context) : String {

        return runBlocking(Dispatchers.IO) {
            val settings = SettingsRepositoryImp(context).settings.first()
            JsonUtil.toJSON(
                mapOf("portNo" to settings.websocketPort)
            )
        }

    }

    @GetMapping("/sensors")
    fun getSensors(context: Context, response: HttpResponse) : String {

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val availableSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL).filter{ it.reportingMode != Sensor.REPORTING_MODE_ONE_SHOT}

        return JsonUtil.toJSON(
            availableSensors.map { sensor ->
                val map = mutableMapOf<String,Any>()
                map["name"] = sensor.name
                map["type"] = sensor.stringType
                return@map map
            }
        )

    }

}