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
package github.umer0586.sensorserver.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import github.umer0586.sensorserver.data.repository.SensorsRepository
import github.umer0586.sensorserver.data.repository.SensorsRepositoryImp
import github.umer0586.sensorserver.data.repository.SettingsRepository
import github.umer0586.sensorserver.data.repository.SettingsRepositoryImp
import github.umer0586.sensorserver.data.service.http.HttpServerServiceBindHelper
import github.umer0586.sensorserver.data.service.http.HttpServerServiceBindHelperImp
import github.umer0586.sensorserver.data.service.websocket.WebsocketServerServiceBindHelper
import github.umer0586.sensorserver.data.service.websocket.WebsocketServerServiceBindHelperImp

@Module
@InstallIn(ViewModelComponent::class)
object ProvideViewModelDeps {

    @Provides
    @ViewModelScoped
    fun provideSettingsRepo(@ApplicationContext context: Context) : SettingsRepository {
        return SettingsRepositoryImp(context)
    }

    @Provides
    @ViewModelScoped
    fun provideWebsocketServerServiceBindHelper(@ApplicationContext context: Context) : WebsocketServerServiceBindHelper {
        return WebsocketServerServiceBindHelperImp(context)
    }

    @Provides
    @ViewModelScoped
    fun provideHttpServerServiceBindHelper(@ApplicationContext context: Context) : HttpServerServiceBindHelper {
        return HttpServerServiceBindHelperImp(context)
    }

    @Provides
    @ViewModelScoped
    fun provideSensorsRepository(@ApplicationContext context: Context) : SensorsRepository {
        return SensorsRepositoryImp(context)
    }
}


