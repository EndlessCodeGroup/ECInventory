/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2022 EndlessCode Group and contributors
 *
 * ECInventory is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ECInventory is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ECInventory. If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.inventory.internal.compat

import org.bukkit.Bukkit
import ru.endlesscode.inventory.internal.util.Log

internal object BukkitVersion {

    private const val VERSION_1_16_5 = 1_16_05
    private const val VERSION_1_19 = 1_19_00

    private val versionRegex = Regex("(?<version>\\d\\.\\d{1,2}(\\.\\d)?)-.*")
    private val version by lazy { initVersionCode() }

    fun checkCompatibility(): Boolean {
        return when {
            version < VERSION_1_16_5 -> {
                Log.e(
                    "Minimal required Bukkit version is 1.16.5 because ECInventory uses libraries",
                    "loading feature. Current version is $version.",
                )
                false
            }

            version >= VERSION_1_19 -> {
                Log.w(
                    "ECInventory is not tested with Bukkit $version,",
                    "please report any problems you find.",
                )
                true
            }

            else -> true
        }
    }

    private fun initVersionCode(): Version {
        val result = checkNotNull(versionRegex.matchEntire(Bukkit.getBukkitVersion()))
        val versionString = checkNotNull(result.groups["version"]?.value)
        return Version.parseVersion(versionString)
    }
}
