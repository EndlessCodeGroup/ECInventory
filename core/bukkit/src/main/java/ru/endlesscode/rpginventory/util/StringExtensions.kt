package ru.endlesscode.rpginventory.util

import org.bukkit.ChatColor

internal fun String.translateColorCodes(colorCode: Char = '&'): String {
    return ChatColor.translateAlternateColorCodes(colorCode, this)
}
