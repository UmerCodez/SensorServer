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
package github.umer0586.sensorserver.data.service.http

import android.content.Context
import github.umer0586.sensorserver.data.util.ServiceBindHelper

class HttpServerServiceBindHelperImp(
    context: Context
) : HttpServerServiceBindHelper {

    private val serviceBindHelper =
        ServiceBindHelper(context, service = HttpServerServiceImp::class.java)

    override fun bind() {
        serviceBindHelper.bindToService()
    }

    override fun unBind() {
        serviceBindHelper.unBindFromService()
    }

    override fun onConnected(callback: (HttpServerService) -> Unit) {

        serviceBindHelper.onServiceConnected { binder ->
            val localBinder = binder as HttpServerServiceImp.LocalBinder
            val httpServerService = localBinder.service
            callback.invoke(httpServerService)
        }
    }
}