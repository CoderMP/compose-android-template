package com.codermp.composeandroidtemplate.core.presentation.ui

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Data class that models a snack bar event.
 * @param message The message to display.
 * @param action The action to perform.
 */
data class SnackBarEvent(
    val message: String,
    val action: SnackBarAction? = null
)

/**
 * Data class that models a snack bar action.
 * @param name The name of the action.
 * @param action The action to perform.
 */
data class SnackBarAction(
    val name: String,
    val action: suspend () -> Unit
)

/**
 * Object class that sends [SnackBarEvent]s.
 */
object SnackBarController {
    private val _events = Channel<SnackBarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackBarEvent) {
        _events.send(element = event)
    }
}