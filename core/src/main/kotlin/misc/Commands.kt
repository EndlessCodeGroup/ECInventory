package ru.endlesscode.rpginventory.misc

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.MultiLiteralArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import ru.endlesscode.rpginventory.CustomInventory
import ru.endlesscode.rpginventory.internal.DI

private val rootCommand get() = CommandAPICommand("inventories").withAliases("inv")

internal fun registerCommand() {
    rootCommand
        .withSubcommand(subcommandOpen())
        .register()
}

private fun subcommandOpen(): CommandAPICommand =
    CommandAPICommand("open")
        .withArguments(inventoryArgument())
        .executesPlayer(PlayerCommandExecutor { sender, (id) ->
            val inventory = CustomInventory(DI.inventories.getValue(id as String))
            inventory.open(sender)
        })

private fun inventoryArgument(): Argument = MultiLiteralArgument(*DI.inventories.keys.toTypedArray())
