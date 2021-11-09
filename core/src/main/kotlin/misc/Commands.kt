package ru.endlesscode.inventory.misc

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.MultiLiteralArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import ru.endlesscode.inventory.CustomInventory
import ru.endlesscode.inventory.internal.DI

private val rootCommand get() = CommandAPICommand("inventories").withAliases("inv")
// TODO: This is temporary solution for debug purpose here should be normal SQL storage
private val inventories = mutableMapOf<String, CustomInventory>()

internal fun registerCommand() {
    rootCommand
        .withSubcommand(subcommandOpen())
        .register()
}

private fun subcommandOpen(): CommandAPICommand =
    CommandAPICommand("open")
        .withArguments(inventoryArgument())
        .executesPlayer(PlayerCommandExecutor { sender, (idArg) ->
            val id = idArg as String
            val inventory = inventories.getOrPut(id) { CustomInventory(DI.inventories.getValue(id)) }
            inventory.open(sender)
        })

private fun inventoryArgument(): Argument = MultiLiteralArgument(*DI.inventories.keys.toTypedArray())
