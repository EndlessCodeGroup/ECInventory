/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021-2022 EndlessCode Group and contributors
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

package ru.endlesscode.inventory.internal

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.MultiLiteralArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.endlesscode.inventory.ECInventoryPlugin
import ru.endlesscode.inventory.internal.di.DI
import ru.endlesscode.inventory.internal.util.sendColorizedMessage

private val rootCommand get() = CommandAPICommand("inventories").withAliases("inv")

internal fun registerCommand(plugin: ECInventoryPlugin) {
    rootCommand
        .withShortDescription("Commands for ECInventory")
        .withSubcommand(subcommandOpen())
        .withSubcommand(subcommandOpenOthers())
        .withSubcommand(subcommandReload(plugin))
        .executes(CommandExecutor { sender, _ ->
            sender.sendColorizedMessage("&2${plugin.name} v${plugin.description.version}")
        })
        .register()
}

private fun subcommandOpen(): CommandAPICommand =
    CommandAPICommand("open")
        .withPermission("ecinventory.open")
        .withArguments(inventoryArgument())
        .executesPlayer(PlayerCommandExecutor { sender, args ->
            openInventory(sender, type = args.first() as String)
        })

private fun subcommandOpenOthers(): CommandAPICommand =
    CommandAPICommand("open")
        .withPermission("ecinventory.open")
        .withArguments(
            inventoryArgument(),
            PlayerArgument("target").withPermission("ecinventory.open.others"),
        )
        .executesPlayer(PlayerCommandExecutor { sender, args ->
            openInventory(sender, type = args.first() as String, target = args[1] as Player)
        })

private fun subcommandReload(plugin: ECInventoryPlugin): CommandAPICommand =
    CommandAPICommand("reload")
        .withPermission("ecinventory.reload")
        .executes(CommandExecutor { sender, _ ->
            val onlinePlayers = Bukkit.getServer().onlinePlayers.size
            if (onlinePlayers > 1) {
                sender.sendColorizedMessage(
                    "&7------------------- &6ATTENTION!&7 -------------------",
                    "&e/inventories reload&6 may lead to unexpected behavior,",
                    "&6use it only for debug purposes when players can't join",
                    "&6to the server.",
                    "&7------------------------------------------------"
                )
            }
            plugin.reload(sender)
        })

private fun inventoryArgument(): Argument = MultiLiteralArgument(*DI.data.inventories.keys.toTypedArray())

private fun openInventory(sender: Player, type: String, target: Player = sender) {
    val inventory = DI.data.inventoriesRepository.getInventory(target, type)
    inventory.open(sender)
}
