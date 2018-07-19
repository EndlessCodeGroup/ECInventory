/*
 * This file is part of RPGInventory.
 * Copyright (C) 2017 EndlessCode Group and contributors
 *
 * RPGInventory is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RPGInventory.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory.configuration;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("WeakerAccess")
public class ConfigurationProvider<T extends Configurable> {

    private final HoconConfigurationLoader loader;
    private final ObjectMapper<T>.BoundInstance configMapper;
    private CommentedConfigurationNode root;
    private String node;
    private String header;
    private T configBase;

    /**
     * Create new instance of the ConfigurationProvider
     *
     * @param configFolder File that points to a directory.
     * @param clazz        A class that extends Configurable.
     */
    public ConfigurationProvider(File configFolder, Class<T> clazz) {
        this(configFolder.toPath(), clazz);
    }

    /**
     * Create new instance of the ConfigurationProvider
     *
     * @param configFolder Path that points to a directory.
     * @param clazz        A class that extends Configurable.
     */
    public ConfigurationProvider(Path configFolder, Class<? extends Configurable> clazz) {
        try {
            if (!Files.exists(configFolder)) {
                Files.createDirectory(configFolder);
            }

            final Configurable object = clazz.newInstance();
            this.node = object.getNodeName();
            this.header = object.getHeader();

            final Path path = configFolder.resolve(object.fileName().concat(".conf"));
            this.loader = HoconConfigurationLoader.builder().setPath(path).build();
            final ObjectMapper<? extends Configurable> objectMapper = ObjectMapper.forClass(clazz);
            //noinspection unchecked - How do we check it?
            this.configMapper = (ObjectMapper<T>.BoundInstance) objectMapper.bindToNew();
            this.reload();
            this.save();
        } catch (ObjectMappingException | IOException | IllegalAccessException | InstantiationException e) {
            throw new ConfigurationException("Failed to initialize configuration!", e);
        }
    }

    public T getConfig() {
        return configBase;
    }

    public void reload() {
        try {
            this.root = this.loader.load(ConfigurationOptions.defaults().setHeader(this.header));
            this.configBase = this.configMapper.populate(this.root.getNode(this.node));
        } catch (ObjectMappingException | IOException e) {
            throw new ConfigurationException("Failed to reload configuration!", e);
        }
    }

    public void save() {
        try {
            this.configMapper.serialize(this.root.getNode(this.node));
            this.loader.save(this.root);
        } catch (ObjectMappingException | IOException e) {
            throw new ConfigurationException("Failed to save configuration!", e);
        }
    }

}
