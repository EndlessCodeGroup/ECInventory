package ru.endlesscode.rpginventory.internal

class InstantTaskScheduler : TaskScheduler {
    override fun runTask(task: () -> Unit) = task()
}
