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

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

class MainConfiguration : Configurable {

    @Setting(comment = "Make sure that you have correctly configured the \"resourcePack\" section before enabling the plugin.")
    var isEnabled = false

    @Setting(comment = "Default locale for use")
    var locale = "en"

    @Setting
    val updates = UpdatesConfiguration()

    @Setting
    val resourcePack = ResourcePackConfiguration()

    override val header: String = "This is RPGInventory configuration blah-blah-blah enjoy new config blah-blah-blah"

    override val nodeName: String = "RPGInventory"
}

@ConfigSerializable
class UpdatesConfiguration {

    @Setting
    var isCheckUpdates = false

    @Setting
    var isDownloadUpdates = false
}

@ConfigSerializable
class ResourcePackConfiguration {

    @Setting(comment = "TODO: Write useful comment")
    var sha = "unknown"

    @Setting(comment = "TODO: Write useless comment")
    var url = "unknown"
}
