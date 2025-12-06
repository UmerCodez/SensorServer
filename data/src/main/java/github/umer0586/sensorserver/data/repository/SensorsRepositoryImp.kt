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
package github.umer0586.sensorserver.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import github.umer0586.sensorserver.data.model.DeviceSensor
import github.umer0586.sensorserver.data.model.toDeviceSensors

class SensorsRepositoryImp(
    val context: Context,
) : SensorsRepository{

    override suspend fun getAvailableSensors(): List<DeviceSensor> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getSensorList(Sensor.TYPE_ALL).filter{ it.reportingMode != Sensor.REPORTING_MODE_ONE_SHOT}.toDeviceSensors()
    }
}