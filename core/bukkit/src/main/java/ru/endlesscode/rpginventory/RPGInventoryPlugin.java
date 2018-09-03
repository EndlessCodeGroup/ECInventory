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

package ru.endlesscode.rpginventory;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.endlesscode.rpginventory.configuration.ConfigurationProvider;
import ru.endlesscode.rpginventory.configuration.MainConfiguration;
import ru.endlesscode.rpginventory.misc.I18N;
import ru.endlesscode.rpginventory.misc.I18NBukkit;

import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * This class is entry point to plugin.
 */
public class RPGInventoryPlugin extends JavaPlugin {

    private ConfigurationProvider<MainConfiguration> configProvider;
    private I18N locale;

    @Override
    public void onEnable() {
        if (!this.loadParts()) {
            return;
        }
        //TODO: Logic
    }

    private boolean loadParts() {
        return makeSure(() -> {
            this.configProvider = new ConfigurationProvider<>(this.getDataFolder(), MainConfiguration.class);
            this.locale = new I18NBukkit(this);
        }, (e) -> {
            this.getLogger().log(Level.SEVERE, "Error on plugin enable.", e);
            this.disable();
        });
    }

    private boolean makeSure(Runnable action, Consumer<RuntimeException> exceptionConsumer) {
        try {
            action.run();
        } catch (RuntimeException e) {
            exceptionConsumer.accept(e);
            return false;
        }

        return true;
    }

    private void disable() {
        this.getServer().getPluginManager().disablePlugin(this);
    }

    public MainConfiguration getConfiguration() {
        return configProvider.getConfig();
    }

    public I18N getLocale() {
        return locale;
    }

    @Override
    @Deprecated
    public FileConfiguration getConfig() {
        throw new UnsupportedOperationException("Use RPGInventoryPlugin#getConfiguration instead of RPGInventoryPlugin#getConfig()");
    }

    @Override
    public void onDisable() {

    }
}
