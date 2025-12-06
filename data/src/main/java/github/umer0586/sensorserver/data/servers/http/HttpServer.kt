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
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import github.umer0586.sensorserver.data.model.http.HttpServerInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.util.concurrent.TimeUnit


class HttpServer(val context : Context, val address : String, val portNo : Int) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var server : Server? = null

    private var onStart : ((HttpServerInfo) -> Unit)? = null
    private var onStop : (() -> Unit)? = null
    private var onError : ((Exception) -> Unit)? = null

    val isRunning get() = server?.isRunning ?: false
    val httpServerInfo get() = HttpServerInfo(address = address, portNo = portNo)

    fun startServer(){
        scope.launch {
            server = AndServer.webServer(context).apply {
                port(portNo)
                timeout(10, TimeUnit.SECONDS)
                inetAddress(InetAddress.getByName(address))
                listener(object : Server.ServerListener{
                    override fun onStarted() {
                        onStart?.invoke(httpServerInfo)
                    }

                    override fun onStopped() {
                        onStop?.invoke()
                    }

                    override fun onException(e: java.lang.Exception?) {
                        e?.let {
                            onError?.invoke(it)
                        }
                    }

                })
            }.build()

            server?.startup()
        }
    }

    fun stopServer(){
        server?.apply {
            if(isRunning){
                shutdown()
                scope.cancel()
            }
        }
    }

    fun setOnStart(onStart : ((HttpServerInfo) -> Unit)?){
        this.onStart = onStart
    }
    fun setOnStop(onStop : (() -> Unit)?){
        this.onStop = onStop
    }
    fun setOnError(onError : ((Exception) -> Unit)?){
        this.onError = onError
    }


}