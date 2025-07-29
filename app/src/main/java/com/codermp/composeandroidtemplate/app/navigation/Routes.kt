package com.codermp.composeandroidtemplate.app.navigation

import kotlinx.serialization.Serializable

/**
 * Sealed interface that defines the routes for the application.
 */
sealed interface Routes {
    /**
     * Routes are defined in one of two ways:
     *
     * ```kotlin
     * @Serializable
     * data object $ROUTE_NAME: Routes
     * ```
     * or
     *
     * ```kotlin
     * @Serializable
     * data class $ROUTE_NAME(val param: Type): Routes
     * ```
     */
    @Serializable
    data object Home : Routes
}