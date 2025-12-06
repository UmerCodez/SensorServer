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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import github.umer0586.sensorserver.data.model.Setting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

//The delegate will ensure that we have a single instance of DataStore with that name in our application.
private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore("settings")

class SettingsRepositoryImp(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SettingsRepository {

    private object Key {
        val WEBSOCKET_PORT = intPreferencesKey("websocket_port")
        val HTTP_PORT = intPreferencesKey("http_port")
        val SAMPLING_RATE = intPreferencesKey("sampling_rate")
        val USE_LOCALHOST = booleanPreferencesKey("use_localhost")
        val USE_HOTSPOT = booleanPreferencesKey("use_hotspot")
        val DISCOVERABLE = booleanPreferencesKey("discoverable")
        val LISTEN_ON_ALL_INTERFACE = booleanPreferencesKey("listen_on_all_interface")
    }

    override val settings: Flow<Setting>
        get() = context.userPreferencesDataStore.data.map { pref ->
            Setting(
               websocketPort = pref[Key.WEBSOCKET_PORT] ?: 8080,
                httpPort = pref[Key.HTTP_PORT] ?: 9090,
                samplingRate = pref[Key.SAMPLING_RATE] ?: 200000,
                useLocalHost = pref[Key.USE_LOCALHOST] ?: false,
                useHotSpot = pref[Key.USE_HOTSPOT] ?: false,
                discoverable = pref[Key.DISCOVERABLE] ?: false,
                listenOnAllInterface = pref[Key.LISTEN_ON_ALL_INTERFACE] ?: false
            )
        }.flowOn(ioDispatcher)


    override suspend fun updateSettings(block: (Setting) -> Setting) {
        val oldSettings = this.settings.first()
        val newSettings = block.invoke(oldSettings)
        saveSettings(newSettings)
    }

    private suspend fun saveSettings(setting: Setting) {

        context.userPreferencesDataStore.edit { pref ->
            pref[Key.WEBSOCKET_PORT] = setting.websocketPort
            pref[Key.HTTP_PORT] = setting.httpPort
            pref[Key.SAMPLING_RATE] = setting.samplingRate
            pref[Key.USE_LOCALHOST] = setting.useLocalHost
            pref[Key.LISTEN_ON_ALL_INTERFACE] = setting.listenOnAllInterface
            pref[Key.USE_HOTSPOT] = setting.useHotSpot
            pref[Key.DISCOVERABLE] = setting.discoverable

        }

    }
}