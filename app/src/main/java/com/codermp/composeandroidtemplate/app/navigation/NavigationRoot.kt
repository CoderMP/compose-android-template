package com.codermp.composeandroidtemplate.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

/**
 * Composable function that defines the root navigation graph for the application.
 * @param navController The [NavHostController] used to control navigation within the app.
 */
@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {
        /**
         * Define your navigation graphs &/or composable routes here.
         */
    }
}