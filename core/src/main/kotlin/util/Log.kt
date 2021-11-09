package ru.endlesscode.inventory.util

import java.util.logging.Level
import java.util.logging.Logger

internal object Log {

    private var logger: Logger? = null

    /** Initializes Log with the given [logger]. */
    fun init(logger: Logger) {
        Log.logger = logger
    }

    /** Writes info message to log. */
    fun i(message: String) {
        logger?.info(message)
    }

    /** Writes warning message to log. */
    fun w(vararg messages: String) {
        messages.forEach { logger?.warning(it) }
    }

    /** Writes error message to log. */
    fun e(message: String, throwable: Throwable) {
        logger?.log(Level.SEVERE, message, throwable)
    }
}
