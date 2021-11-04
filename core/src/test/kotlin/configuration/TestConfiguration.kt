/*
 * This file is part of RPGInventory3.
 * Copyright (C) 2019 EndlessCode Group and contributors
 *
 * RPGInventory3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with RPGInventory3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory.configuration

import kotlinx.serialization.Serializable

@Serializable
internal data class TestConfiguration(
    var aString: String = "Lorem ipsum dolor sit amet.",
    var anInt: Int = 5,
) {

    companion object {
        val SERIALIZER = ConfigurationSerializer<TestConfiguration> { config ->
            mapOf(
                "a-string" to config.aString,
                "an-int" to config.anInt,
            )
        }
    }
}
