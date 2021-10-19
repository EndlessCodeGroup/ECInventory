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

import ninja.leaping.configurate.ConfigurationOptions
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ru.endlesscode.rpginventory.misc.makeSureDirectoryExists
import java.io.File
import java.nio.file.Path

/**
 * ConfigurationProvider helps to load and save [config].
 *
 * @param configFolder Path that points to a config directory.
 * @param clazz        A class that extends Configurable.
 */
class ConfigurationProvider<T : Configurable>(configFolder: Path, clazz: Class<T>) {

    companion object {
        /**
         * Creates new instance of the ConfigurationProvider.
         * Simplified "constructor" for Kotlin to avoid class argument.
         */
        inline operator fun <reified T : Configurable> invoke(configPath: Path): ConfigurationProvider<T> {
            return ConfigurationProvider(configPath, T::class.java)
        }
    }

    private val loader: HoconConfigurationLoader
    private val configMapper: ObjectMapper<T>.BoundInstance
    private val node: String
    private val header: String?

    private var root: CommentedConfigurationNode

    var config: T
        private set

    /**
     * Creates new instance of the ConfigurationProvider.
     *
     * @param configFolder File that points to a directory.
     * @param clazz        A class that extends Configurable.
     */
    constructor(configFolder: File, clazz: Class<T>) : this(configFolder.toPath(), clazz)

    init {
        try {
            configFolder.makeSureDirectoryExists()

            val configurable = clazz.getDeclaredConstructor().newInstance()
            this.node = configurable.nodeName
            this.header = configurable.header
            this.loader = buildConfigurationLoader(configFolder, configurable.fileName)
            this.configMapper = ObjectMapper.forClass(clazz).bindToNew()

            // We can't use reload() because we should initialize `root` and `config` in `init`
            // to make it not nullable and prevent lateinit usage.
            reloadInternal().let { (root, config) ->
                this.root = root
                this.config = config
            }

            this.save()
        } catch (e: Exception) {
            configError("Failed to initialize configuration!", e)
        }
    }

    private fun buildConfigurationLoader(folder: Path, fileName: String): HoconConfigurationLoader {
        val path = folder.resolve("$fileName$CONFIG_EXTENSION")
        return HoconConfigurationLoader.builder().setPath(path).build()
    }

    /** Reloads [config]. */
    fun reload() {
        try {
            reloadInternal().let { (root, config) ->
                this.root = root
                this.config = config
            }
        } catch (e: Exception) {
            configError("Failed to reload configuration!", e)
        }
    }

    private fun reloadInternal(): Pair<CommentedConfigurationNode, T> {
        val root = loader.load(ConfigurationOptions.defaults().setHeader(header))
        val config = configMapper.populate(root.getNode(node))
        return root to config
    }

    /** Saves [config]. */
    fun save() {
        try {
            configMapper.serialize(root.getNode(node))
            loader.save(root)
        } catch (e: Exception) {
            configError("Failed to save configuration!", e)
        }
    }
}
