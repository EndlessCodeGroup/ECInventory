package ru.endlesscode.inventory.internal

class InstantTaskScheduler : TaskScheduler {
    override fun runTask(task: () -> Unit) = task()
}
