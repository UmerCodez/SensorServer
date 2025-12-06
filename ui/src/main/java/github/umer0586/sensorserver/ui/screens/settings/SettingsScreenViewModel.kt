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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import github.umer0586.sensorserver.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel(){

    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state.asStateFlow()


    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update {
                    it.copy(setting = settings)
                }
            }
        }
    }



    fun onEvent(event: SettingsScreenEvent) {
        when(event) {
            is SettingsScreenEvent.OnDiscoverableChange -> {
                viewModelScope.launch {
                    settingsRepository.updateSettings {
                        it.copy(discoverable = event.discoverable)
                    }
                }
            }
            is SettingsScreenEvent.OnHttpPortChange -> {
                viewModelScope.launch {
                    settingsRepository.updateSettings {
                        it.copy(httpPort = event.httpPortNo)
                    }
                }
            }
            is SettingsScreenEvent.OnListenOnAllInterfaceChange -> {
                viewModelScope.launch {
                    settingsRepository.updateSettings {
                        it.copy(listenOnAllInterface = event.listenOnAllInterface)
                    }

                    if(event.listenOnAllInterface){
                        settingsRepository.updateSettings {
                            it.copy(
                                useLocalHost = false,
                                useHotSpot = false
                            )
                        }
                    }
                }
            }
            is SettingsScreenEvent.OnSamplingRateChange -> {
                viewModelScope.launch {
                    settingsRepository.updateSettings {
                        it.copy(samplingRate = event.samplingRate)
                    }
                }
            }
            is SettingsScreenEvent.OnUseHotSpotChange -> {
                viewModelScope.launch {
                    settingsRepository.updateSettings {
                        it.copy(useHotSpot = event.useHotSpot)
                    }
                    if(event.useHotSpot){
                        settingsRepository.updateSettings {
                            it.copy(
                                useLocalHost = false,
                                listenOnAllInterface = false
                            )
                        }
                    }
                }
            }
            is SettingsScreenEvent.OnUseLocalHostChange -> {
                viewModelScope.launch {
                    settingsRepository.updateSettings {
                        it.copy(useLocalHost = event.useLocalHost)
                    }

                    if(event.useLocalHost){
                        settingsRepository.updateSettings {
                            it.copy(
                                useHotSpot = false,
                                listenOnAllInterface = false
                            )
                        }
                    }
                }
            }
            is SettingsScreenEvent.OnWebsocketPortChange -> {
                viewModelScope.launch {
                    settingsRepository.updateSettings {
                        it.copy(websocketPort = event.websocketPortNo)
                    }
                }
            }
        }
    }

}