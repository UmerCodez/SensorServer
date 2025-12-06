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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.hardware.Sensor
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import github.umer0586.sensorserver.data.model.toDeviceSensor
import github.umer0586.sensorserver.data.model.toDeviceSensors
import github.umer0586.sensorserver.data.model.websocket.GPSWebsocketClient
import github.umer0586.sensorserver.data.model.websocket.MultipleSensorWebsocketClient
import github.umer0586.sensorserver.data.model.websocket.ServiceRegistrationState
import github.umer0586.sensorserver.data.model.websocket.SingleSensorWebsocketClient
import github.umer0586.sensorserver.data.model.websocket.TouchScreenWebsocketClient
import github.umer0586.sensorserver.data.model.websocket.WebsocketClient
import github.umer0586.sensorserver.data.model.websocket.WebsocketServerState
import github.umer0586.sensorserver.data.repository.SettingsRepository
import github.umer0586.sensorserver.data.servers.websocket.GPS
import github.umer0586.sensorserver.data.servers.websocket.SensorWebSocketServer
import github.umer0586.sensorserver.data.servers.websocket.TouchSensors
import github.umer0586.sensorserver.data.util.getHotspotIp
import github.umer0586.sensorserver.data.util.getIp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.UnknownHostException
import javax.inject.Inject


@AndroidEntryPoint
class WebsocketServerServiceImp : Service(), WebsocketServerService {


    @Inject
    lateinit var settingsRepository: SettingsRepository


    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var sensorWebSocketServer: SensorWebSocketServer? = null

    private val _websocketServerState =  MutableSharedFlow<WebsocketServerState>(replay = 1)
    override val websocketServerState: SharedFlow<WebsocketServerState>
        get() = _websocketServerState


    private val _connectedClients =  MutableSharedFlow<List<WebsocketClient>>(replay = 1)

    override val connectedClients: SharedFlow<List<WebsocketClient>>
        get() = _connectedClients

    private val _serviceRegistrationState =  MutableSharedFlow<ServiceRegistrationState>(replay = 1)
    override val serviceRegistrationState: SharedFlow<ServiceRegistrationState>
        get() = _serviceRegistrationState

    private lateinit var nsdManager : NsdManager


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
        nsdManager = (getSystemService(NSD_SERVICE) as NsdManager)
        createNotificationChannel()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        handleAndroid8andAbove()

        scope.launch {
            startWebsocketServer()
        }

        return START_NOT_STICKY
    }

    private suspend fun startWebsocketServer() {

        val setting = settingsRepository.settings.first()
        val noOptionSelected =
            !(setting.useLocalHost || setting.useHotSpot || setting.listenOnAllInterface)

          val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

          val ipAddress: String? = when {
              setting.useLocalHost -> "127.0.0.1"
              setting.listenOnAllInterface -> "0.0.0.0"
              setting.useHotSpot -> wifiManager.getHotspotIp() // could be null
              else -> wifiManager.getIp() // could be null

          }

          if (ipAddress == null) {

              scope.launch {
                  _websocketServerState.emit(WebsocketServerState.Error(UnknownHostException("Unable to obtain IP")))
                  _websocketServerState.emit(WebsocketServerState.Stopped)
              }

              // Not calling a handleAndroid8andAbove() immediately after onStartCommand
              // would cause application to crash as we are not calling startForeground() here before returning
              stopForeground()
              return
          }

          sensorWebSocketServer = SensorWebSocketServer(
              applicationContext,
              InetSocketAddress(ipAddress, setting.websocketPort)
          )

          sensorWebSocketServer?.onStart { serverInfo ->

              scope.launch {
                  _websocketServerState.emit(WebsocketServerState.Running(serverInfo))
              }

              val activityIntent = Intent().apply {
                  component = ComponentName(
                      "github.umer0586.sensorserver", // Replace with your actual app package name
                      "github.umer0586.sensorserver.MainActivity" // Fully qualified name of your MainActivity
                  )
                  flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
              }

              // Intent to be broadcast (when user press action button in notification)
//              val broadcastIntent = Intent(ACTION_STOP_SERVER).apply {
                  // In Android 14, Intent with custom action must explicitly set package
                  // otherwise Broadcast receiver with RECEIVER_NOT_EXPORTED flag will not receive it
//                  setPackage(packageName)
//              }

              // create a pending intent that can invoke an activity (use to open activity from notification message)
              val pendingIntentActivity = PendingIntent.getActivity(applicationContext, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE)

              // create a pending intent that can fire broadcast (use to send broadcast when user taps action button from notification)
//              val pendingIntentBroadcast = PendingIntent.getBroadcast(applicationContext,0,broadcastIntent,PendingIntent.FLAG_IMMUTABLE)

              val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                  .apply {
                      setSmallIcon(android.R.drawable.btn_plus)
                      setContentTitle("Sensor Server Running...")
                      setContentText("ws://" + serverInfo.ipAddress + ":" + serverInfo.port)
                      setPriority(NotificationCompat.PRIORITY_DEFAULT)
                      setContentIntent(pendingIntentActivity) // Set the intent that will fire when the user taps the notification
 //                     addAction(android.R.drawable.ic_lock_power_off,"stop", pendingIntentBroadcast)
                      setAutoCancel(false) // don't cancel notification when user taps it
                  }


              val notification = notificationBuilder.build()
              startForeground(ON_GOING_NOTIFICATION_ID, notification)

              if(setting.discoverable)
                  makeServiceDiscoverable(serverInfo.port)


          }


          sensorWebSocketServer?.onStop {

              //serverStateListener?.onServerStopped()
              scope.launch {
                  _websocketServerState.emit(WebsocketServerState.Stopped)
              }

              //remove the service from foreground but don't stop (destroy) the service
              //stopForeground(true)
              stopForeground()

              if(setting.discoverable)
                  makeServiceNotDiscoverable()
          }

          sensorWebSocketServer?.onError { exception ->

              //serverStateListener?.onServerError(exception)
              scope.launch {
                  _websocketServerState.emit(WebsocketServerState.Error(exception))
              }

              //stopForeground(true)
              stopForeground()
          }

          sensorWebSocketServer?.onConnectionsChange { webSockets ->

              //connectionsChangeCallBack?.invoke(webSockets)
              //connectionsCountChangeCallBack?.invoke(webSockets.size)

              scope.launch {
                  val websocketClientsList = mutableListOf<WebsocketClient>()
                  webSockets.forEach { websocket ->

                      val address = websocket.remoteSocketAddress.address.hostName
                      val port = websocket.remoteSocketAddress.port

                      if(websocket.getAttachment<Any>() is Sensor){
                          val deviceSensor = (websocket.getAttachment<Any>() as Sensor).toDeviceSensor()
                          websocketClientsList.add(SingleSensorWebsocketClient(address = address, port = port, deviceSensor = deviceSensor, websocket = websocket))
                      }
                      else if(websocket.getAttachment<Any>() is TouchSensors){
                          websocketClientsList.add(TouchScreenWebsocketClient(address = address, port = port, websocket = websocket))
                      }
                      else if(websocket.getAttachment<Any>() is ArrayList<*>){
                          val deviceSensors = (websocket.getAttachment<Any>() as ArrayList<Sensor>).toDeviceSensors()
                          websocketClientsList.add(
                              MultipleSensorWebsocketClient(
                                  address = address,
                                  port = port,
                                  deviceSensors = deviceSensors,
                                  websocket = websocket
                              )
                          )
                      }
                      else if(websocket.getAttachment<Any>() is GPS) {
                          websocketClientsList.add(
                              GPSWebsocketClient(
                                  address = address,
                                  port = port,
                                  websocket = websocket
                              )
                          )

                      }

                  }

                  _connectedClients.emit(websocketClientsList)

              }


          }

          sensorWebSocketServer?.samplingRate = setting.samplingRate
          sensorWebSocketServer?.run()


    }

    override fun startServer() {
        ContextCompat.startForegroundService(
            applicationContext, Intent(
                applicationContext,
                WebsocketServerServiceImp::class.java
            )
        )
    }

    override fun stopServer() {
        sensorWebSocketServer?.also { server ->

            if (server.isRunning) {
                try {
                    server.stop()
                    stopForeground()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun closeConnection(websocketClient: WebsocketClient) {
       try {
           websocketClient.websocket?.close()
       }catch ( e : Exception){
           e.printStackTrace()
       }
    }

    override fun sendMotionEvent(motionEvent: MotionEvent) {
        sensorWebSocketServer?.onMotionEvent(motionEvent)
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
        val tempNotificationId = 421

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val tempNotification = NotificationCompat.Builder(
                applicationContext, CHANNEL_ID
            )
                .setSmallIcon(android.R.drawable.stat_notify_sync)
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

    // Binder given to clients
    private val binder: IBinder = LocalBinder()

    override fun onBind(intent: Intent) = binder

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {


        // Return this instance of LocalService so clients can call public methods
        val service: WebsocketServerServiceImp
            get() = this@WebsocketServerServiceImp // Return this instance of LocalService so clients can call public methods

    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun makeServiceDiscoverable(portNo : Int){
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = "SensorServer"
            serviceType = "_websocket._tcp"
            port = portNo
        }
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, serviceRegistrationListener)
        //serviceRegistrationCallBack?.invoke(ServiceRegistrationState.REGISTERING,serviceInfo,null)
        scope.launch {
            _serviceRegistrationState.emit(ServiceRegistrationState.REGISTERING)
        }
    }

    private fun makeServiceNotDiscoverable(){
        nsdManager.unregisterService(serviceRegistrationListener)
        //serviceRegistrationCallBack?.invoke(ServiceRegistrationState.UNREGISTERING,null,null)
        scope.launch {
            _serviceRegistrationState.emit(ServiceRegistrationState.UNREGISTERING)
        }
    }

    private val serviceRegistrationListener = object : NsdManager.RegistrationListener {

        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
            //serviceRegistrationCallBack?.invoke(ServiceRegistrationState.REGISTRATION_FAIL,serviceInfo,errorCode)
            scope.launch {
                _serviceRegistrationState.emit(ServiceRegistrationState.REGISTRATION_FAIL)
            }
        }

        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
            //serviceRegistrationCallBack?.invoke(ServiceRegistrationState.UNREGISTRATION_FAIL,serviceInfo,errorCode)
            scope.launch {
                _serviceRegistrationState.emit(ServiceRegistrationState.UNREGISTRATION_FAIL)
            }
        }

        override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
            //serviceRegistrationCallBack?.invoke(ServiceRegistrationState.REGISTRATION_SUCCESS,serviceInfo,null)
            scope.launch {
                _serviceRegistrationState.emit(ServiceRegistrationState.REGISTRATION_SUCCESS)
            }
        }

        override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {
            //serviceRegistrationCallBack?.invoke(ServiceRegistrationState.UNREGISTRATION_SUCCESS,serviceInfo,null)
            scope.launch {
                _serviceRegistrationState.emit(ServiceRegistrationState.UNREGISTRATION_SUCCESS)
            }
        }

    }

    companion object {
        private val TAG: String = WebsocketServerServiceImp::class.java.getSimpleName()
        const val CHANNEL_ID = "ForegroundServiceChannel"

        // cannot be zero
        const val ON_GOING_NOTIFICATION_ID = 332
    }

}