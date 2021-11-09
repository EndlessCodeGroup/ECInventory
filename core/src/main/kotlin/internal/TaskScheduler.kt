package ru.endlesscode.inventory.internal

import org.bukkit.plugin.Plugin

internal interface TaskScheduler {
    fun runTask(task: () -> Unit)
}

internal class PluginTaskScheduler(private val plugin: Plugin) : TaskScheduler {

    private val scheduler
        get() = plugin.server.scheduler

    override fun runTask(task: () -> Unit) {
        scheduler.runTask(plugin, task)
    }
}
