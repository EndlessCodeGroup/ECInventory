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

package ru.endlesscode.rpginventory.misc;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import ru.endlesscode.rpginventory.RPGInventoryPlugin;

public class I18NBukkit extends I18N {

    public I18NBukkit(RPGInventoryPlugin instance) {
        super(instance.getDataFolder(), instance.getConfiguration().getLocale());
    }

    @NotNull
    @Override
    public String stripColor(String message) {
        return ChatColor.stripColor(message);
    }

    @NotNull
    @Override
    public String translateCodes(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
