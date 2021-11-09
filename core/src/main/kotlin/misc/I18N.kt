/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2019-2021 EndlessCode Group and contributors
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

package ru.endlesscode.inventory.misc

import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.text.MessageFormat
import java.util.*
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import kotlin.io.path.readText

abstract class I18N protected constructor(workDir: Path, langCode: String) {

    private val localeFolder: Path

    private val locale = Properties()
    private val cache = hashMapOf<String, MessageFormat>()

    protected constructor(workDir: File, langCode: String) : this(workDir.toPath(), langCode)

    init {
        try {
            localeFolder = workDir.resolve("locales").createDirectories()
        } catch (e: IOException) {
            throw I18NException("Failed to create locales folder", e)
        }

        load(langCode)
    }

    fun reload(langCode: String) {
        load(langCode)
        cache.clear()
    }

    private fun load(langCode: String) {
        val localeFile = prepareLocaleFile(langCode.lowercase())
        try {
            localeFile.readText().reader().use(locale::load)
        } catch (e: IOException) {
            throw I18NException("Failed to load ${localeFile.fileName}", e)
        }
    }

    private fun prepareLocaleFile(langCode: String): Path {
        val localeFile = localeFolder.resolve("$langCode.lang")
        if (localeFile.notExists()) {
            localeFile.loadFromResource("/locales/$langCode.lang")
        }

        return localeFile
    }

    fun getMessage(key: String, vararg args: Any): String {
        return getMessage(key, false, *args)
    }

    @JvmOverloads
    fun getMessage(key: String, stripColor: Boolean = false, vararg args: Any = emptyArray()): String {
        val result = getMessageFromCache(key).format(args)
        return if (stripColor) stripColor(result) else result
    }

    private fun getMessageFromCache(key: String): MessageFormat {
        return cache.getOrPut(key) { MessageFormat(translateCodes(locale.getProperty(key, key))) }
    }

    abstract fun stripColor(message: String): String

    abstract fun translateCodes(message: String): String
}
