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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import github.umer0586.sensorserver.data.model.http.HttpServerInfo
import github.umer0586.sensorserver.data.model.http.HttpServerState
import github.umer0586.sensorserver.data.repository.SettingsRepository
import github.umer0586.sensorserver.data.servers.http.HttpServer
import github.umer0586.sensorserver.data.util.getHotspotIp
import github.umer0586.sensorserver.data.util.getIp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@AndroidEntryPoint
class HttpServerServiceImp : Service(), HttpServerService {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private var httpServer: HttpServer? = null;
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    //private var serverStateListener: HttpServerStateListener? = null
    private val _httpServerState = MutableSharedFlow<HttpServerState>(replay = 1)

    override val httpServerState: MutableSharedFlow<HttpServerState>
        get() = _httpServerState


    //private lateinit var appSettings: AppSettings

    // Binder given to clients
    private val binder: IBinder = LocalBinder()




    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
        createNotificationChannel()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "onStartCommand()")
        handleAndroid8andAbove()

        scope.launch {
            startHttpServer()
        }



        return START_NOT_STICKY
    }

    override fun startServer() {
        ContextCompat.startForegroundService(
            applicationContext, Intent(
                applicationContext,
                HttpServerServiceImp::class.java
            )
        )
    }

    override fun stopServer() {
        httpServer?.stopServer()
    }

    private suspend fun startHttpServer() {

        val settings = settingsRepository.settings.first()

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val ipAddress: String? = when {
            settings.useLocalHost -> "127.0.0.1"
            settings.listenOnAllInterface -> "0.0.0.0"
            settings.useHotSpot -> wifiManager.getHotspotIp() // could be null
            else -> wifiManager.getIp() // could be null

        }

        if (ipAddress == null) {
            //serverStateListener?.onError(UnknownHostException("Unable to obtain hotspot IP"))
            scope.launch {
                _httpServerState.emit(HttpServerState.Error(UnknownHostException("Unable to obtain IP")))
                delay(1000)
                _httpServerState.emit(HttpServerState.Stopped)
            }

            // Not calling a handleAndroid8andAbove() immediately after onStartCommand
            // would cause application to crash as we are not calling startForeground() here before returning
            stopForeground()
            return
        }



        httpServer = HttpServer(
            context = applicationContext,
            address = ipAddress,
            portNo = settings.httpPort
        )

        httpServer?.setOnStart { serverInfo ->
            onStarted(serverInfo)
            scope.launch {
                _httpServerState.emit(HttpServerState.Running(serverInfo))
            }
            //serverStateListener?.onStart(serverInfo)
        }

        httpServer?.setOnStop {
            httpServer?.apply {
                //serverStateListener?.onStop()
                scope.launch {
                    _httpServerState.emit(HttpServerState.Stopped)
                }
                //remove the service from foreground but don't stop (destroy) the service
                //stopForeground(true)
                stopForeground()
            }
        }

        httpServer?.setOnError { exception ->
            //serverStateListener?.onError(exception)
            scope.launch {
                _httpServerState.emit(HttpServerState.Error(exception))
            }
            //remove the service from foreground but don't stop (destroy) the service
            //stopForeground(true)
            stopForeground()
        }


        httpServer?.startServer()
    }

    // http server onStart callback
    private fun onStarted(serverHttpServerInfo: HttpServerInfo) {


        // intent to start activity
        val activityIntent = Intent().apply {
            component = ComponentName(
                "github.umer0586.sensorserver", // Replace with your actual app package name
                "github.umer0586.sensorserver.MainActivity" // Fully qualified name of your MainActivity
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }


        // create a pending intent that can invoke an activity (use to open activity from notification message)
        val pendingIntentActivity =
            PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE)


        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .apply {
                setSmallIcon(android.R.drawable.ic_menu_view) // TODO change this icon later
                setContentTitle("Web server Running...")
                setContentText(serverHttpServerInfo.baseUrl)
                setPriority(NotificationCompat.PRIORITY_DEFAULT)
                setContentIntent(pendingIntentActivity) // Set the intent that will fire when the user taps the notification
                setAutoCancel(false) // don't cancel notification when user taps it
            }


        val notification = notificationBuilder.build()
        startForeground(ON_GOING_NOTIFICATION_ID, notification)

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "createNotificationChannel() called")
            val name: CharSequence = "Sensor-Server"
            val description = "Notifications from SensorServer"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /*
     * For Android 8 and above there is a framework restriction which required service.startForeground()
     * method to be called within five seconds after call to Context.startForegroundService()
     * so make sure we call this method even if we are returning from service.onStartCommand() without calling
     * service.startForeground()
     *
     * */
    private fun handleAndroid8andAbove() {
        val tempNotificationId = 651

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val tempNotification = NotificationCompat.Builder(
                applicationContext, CHANNEL_ID
            )
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("")
                .setContentText("").build()
            startForeground(tempNotificationId, tempNotification)
            //stopForeground(true)
            stopForeground()
        }
    }

    @Suppress("DEPRECATION")
    private fun stopForeground() {
        /*
        If the device is running an older version of Android,
        we fallback to stopForeground(true) to remove the service from the foreground and dismiss the ongoing notification.
        Although it shows as deprecated, it should still work as expected on API level 21 (Android 5).
         */

        // for Android 7 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            stopForeground(STOP_FOREGROUND_REMOVE)
        else
        // This method was deprecated in API level 33.
        // Ignore deprecation message as there is no other alternative method for Android 6 and lower
            stopForeground(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")

        scope.cancel()
        httpServer?.stopServer()

    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {

        // Return this instance of LocalService so clients can call public methods
        val service: HttpServerServiceImp
            get() = this@HttpServerServiceImp // Return this instance of LocalService so clients can call public methods

    }


    companion object {

        private val TAG: String = HttpServerServiceImp::class.java.getSimpleName()
        const val CHANNEL_ID = "HTTP-service-channel"

        // cannot be zero
        const val ON_GOING_NOTIFICATION_ID = 934
    }


}