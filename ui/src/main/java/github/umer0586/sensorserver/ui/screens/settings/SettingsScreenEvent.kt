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
package github.umer0586.sensorserver.ui.screens.settings

sealed interface SettingsScreenEvent {
    data class OnWebsocketPortChange(val websocketPortNo: Int) : SettingsScreenEvent
    data class OnHttpPortChange(val httpPortNo: Int) : SettingsScreenEvent
    data class OnSamplingRateChange(val samplingRate: Int) : SettingsScreenEvent
    data class OnListenOnAllInterfaceChange(val listenOnAllInterface: Boolean) : SettingsScreenEvent
    data class OnUseHotSpotChange(val useHotSpot: Boolean) : SettingsScreenEvent
    data class OnUseLocalHostChange(val useLocalHost: Boolean) : SettingsScreenEvent
    data class OnDiscoverableChange(val discoverable: Boolean) : SettingsScreenEvent
}
