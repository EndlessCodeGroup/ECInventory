/*
 * This file is part of ECInventory
 * <https://github.com/EndlessCodeGroup/ECInventory>.
 * Copyright (c) 2021 EndlessCode Group and contributors
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
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import ru.endlesscode.inventory.internal.di.DI

private val rootCommand get() = CommandAPICommand("inventories").withAliases("inv")

internal fun registerCommand() {
    rootCommand
        .withSubcommand(subcommandOpen())
        .register()
}

private fun subcommandOpen(): CommandAPICommand =
    CommandAPICommand("open")
        .withArguments(inventoryArgument())
        .executesPlayer(PlayerCommandExecutor { sender, args ->
            val type = args.first() as String
            val inventory = DI.data.inventoriesRepository.getInventory(sender, type)
            inventory.open(sender)
        })

private fun inventoryArgument(): Argument = MultiLiteralArgument(*DI.data.inventories.keys.toTypedArray())
