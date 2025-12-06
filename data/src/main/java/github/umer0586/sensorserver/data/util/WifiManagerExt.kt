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

import android.net.wifi.WifiManager
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.math.BigInteger
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException
import java.nio.ByteOrder

@Suppress("DEPRECATION")
fun WifiManager.getIp(): String? {
    var ipAddress = this.connectionInfo.ipAddress

    // Convert little-endian to big-endianif needed
    if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
        ipAddress = Integer.reverseBytes(ipAddress)
    }
    val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
    val ipAddressString: String? = try {
        InetAddress.getByAddress(ipByteArray).hostAddress
    } catch (ex: UnknownHostException) {
        null
    }
    return ipAddressString
}

fun WifiManager.getHotspotIp(): String? {
    if (this.isHotSpotEnabled() == false)
        return null

    var ipAddress: String? = null
    try {
        val enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (enumNetworkInterfaces.hasMoreElements()) {
            val networkInterface = enumNetworkInterfaces.nextElement()
            val enumInetAddress = networkInterface.inetAddresses
            while (enumInetAddress.hasMoreElements()) {
                val inetAddress = enumInetAddress.nextElement()
                if (inetAddress.isSiteLocalAddress) {
                    ipAddress = inetAddress.hostAddress
                }
            }
        }
    } catch (e: SocketException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
        ipAddress = null
    }
    return ipAddress
}

fun WifiManager.isHotSpotEnabled(): Boolean {

    val wmMethods: Array<Method> = this.javaClass.getDeclaredMethods()

    //is wifi access point (hotspot) enabled
    var isWifiAPenabled = false
    for (method in wmMethods) {
        if (method.name == "isWifiApEnabled") {
            try {
                method.isAccessible = true //in the case of visibility change in future APIs
                isWifiAPenabled = method.invoke(this) as Boolean
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
    }
    return isWifiAPenabled
}