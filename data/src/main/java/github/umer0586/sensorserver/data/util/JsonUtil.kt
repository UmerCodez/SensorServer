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

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException

object JsonUtil {


    private val objectMapper = ObjectMapper()
    fun toJSON(`object`: Any?): String {
        var json = ""
        try {
            json = objectMapper.writer().writeValueAsString(`object`)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json
    }

    // Maps string elements in JSON array to Java list e.g [a,b,c]
    fun readJSONArray(jsonArrayString: String?): List<String>? {
        try {
            return objectMapper.readValue<List<String>>(jsonArrayString, object :
                TypeReference<List<String>?>() {})
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}