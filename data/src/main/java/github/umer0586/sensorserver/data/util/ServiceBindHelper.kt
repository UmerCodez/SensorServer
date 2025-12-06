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
package github.umer0586.sensorserver.data.util

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

open class ServiceBindHelper(
    private val context: Context,
    private val service: Class<out Service>
) : ServiceConnection {

    private var bounded = false
    private var onServiceConnectedCallBack: ((IBinder) -> Unit)? = null

    fun bindToService() {
        Log.d(TAG, "bindToService()")
        val intent = Intent(context, service)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
        bounded = true
    }

    fun unBindFromService() {
        Log.d(TAG, "unBindFromService()")
        if (bounded) {
            context.unbindService(this)
            bounded = false
        }
    }

    open fun onServiceConnected(callBack: ((IBinder) -> Unit)?) {
        onServiceConnectedCallBack = callBack
    }


    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        Log.d(TAG, "onServiceConnected()")
        bounded = true

        onServiceConnectedCallBack?.invoke(binder)

    }

    /**  The onServiceDisconnected() method in Android is called when the connection to the service is unexpectedly disconnected,
     *   usually due to a crash or the service being killed by the system.
     *   This allows you to handle the situation and possibly attempt to reestablish the connection.
     *   onServiceDisconnected() method is not called when you explicitly call context.unbindService().
     *   It's only called when the connection to the service is unexpectedly lost, such as when the service process crashes or is killed by the system.
     *   */
    override fun onServiceDisconnected(name: ComponentName) {
        Log.d(TAG, "onServiceDisconnected()")
        bounded = false
        bindToService()

    }


    companion object {
        private val TAG: String = ServiceBindHelper::class.java.simpleName
    }

}