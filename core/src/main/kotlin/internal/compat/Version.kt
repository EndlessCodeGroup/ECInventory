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

/**
 * Constructs Version object from the given data according to semantic versioning.
 *
 * @param major     Major version.
 * @param minor     Minor version.
 * @param patch     Patch version.
 * @param qualifier Qualifier, or empty string if qualifier not exists.
 * @throws IllegalArgumentException when passed negative version codes.
 */
internal data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val qualifier: String = "",
) : Comparable<Version> {

    /**
     * Returns version code in format xxyyzz, where x - major version, y - minor and z - patch.
     * Example:
     * 1.12.2  ->  11202
     * 21.0.12 -> 210012
     * 1.9     ->  10900
     * 2.2.1   ->  20201
     * Major, minor and patch versions shouldn't be higher than 99.
     */
    val versionCode: Int
        get() = major * 10000 + minor * 100 + patch

    init {
        require(major >= 0 && minor >= 0 && patch >= 0) { "Version can't include negative numbers" }
    }

    operator fun compareTo(other: Int): Int = versionCode.compareTo(other)
    operator fun compareTo(other: String): Int = this.compareTo(parseVersion(other))

    override operator fun compareTo(other: Version): Int = COMPARATOR.compare(this, other)

    override fun toString(): String {
        val qualifier = if (qualifier.isEmpty()) "" else "-$qualifier"
        return "$major.$minor.$patch$qualifier"
    }

    companion object {

        private val COMPARATOR = compareBy<Version> { it.major }
            .thenBy { it.minor }
            .thenBy { it.patch }

        /**
         * Parses version from the given string.
         *
         * @param version String representation of version.
         * @return The constructed version.
         * @throws IllegalArgumentException If passed string with wrong format.
         */
        fun parseVersion(version: String): Version {
            var parts = version.split("-", limit = 2)
            val qualifier = if (parts.size > 1) parts[1] else ""

            parts = parts[0].split("\\.", limit = 3)
            return try {
                val patch = if (parts.size > 2) parts[2].toInt() else 0
                val minor = if (parts.size > 1) parts[1].toInt() else 0
                val major = parts[0].toInt()
                Version(major, minor, patch, qualifier)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Can not parse version string: $version", e)
            }
        }
    }
}
