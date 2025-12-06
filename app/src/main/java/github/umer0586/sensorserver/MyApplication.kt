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
package github.umer0586.sensorserver

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import org.acra.BuildConfig
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

@HiltAndroidApp
class MyApplication : Application(){

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        initAcra {
            //core configuration:
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON


            mailSender {
                //required
                mailTo = "umerfarooq2383@gmail.com"
                //defaults to true
                reportAsFile = true
                //defaults to ACRA-report.stacktrace
                reportFileName = "SensorServer-CrashLog.txt"
                //defaults to "<applicationId> Crash Report"
                subject = "SensorServer Crash report"
                //defaults to empty
                body = "Crash report attached"
            }


        }

    }

}