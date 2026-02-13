@file:Suppress("MaxLineLength")

package com.codermp.composeandroidtemplate.core.presentation.ui

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

/**
 * Composable function that returns the current device configuration.
 */
@Composable
fun currentDeviceConfiguration(): DeviceConfiguration {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return DeviceConfiguration.fromWindowSizeClass(windowSizeClass = windowSizeClass)
}

/**
 * Represents the configuration of the device based on the window size class.
 *
 * This enum class categorizes devices into different configurations based on their
 * width and height size classes, which can be used to adapt UI layouts accordingly.
 */
enum class DeviceConfiguration {
    MOBILE_PORTRAIT,
    MOBILE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE,
    DESKTOP;

    val isMobile: Boolean
        get() = this in listOf(MOBILE_PORTRAIT, MOBILE_LANDSCAPE)

    val isWideScreen: Boolean
        get() = this in listOf(TABLET_LANDSCAPE, DESKTOP)

    companion object {
        /**
         * Function that returns the appropriate [DeviceConfiguration] based on the provided [windowSizeClass].
         * @param windowSizeClass The [WindowSizeClass] of the device.
         * @return The corresponding [DeviceConfiguration] based on the window size class.
         */
        fun fromWindowSizeClass(windowSizeClass: WindowSizeClass): DeviceConfiguration {
            return with(receiver = windowSizeClass) {
                when {
                    minWidthDp < WIDTH_DP_MEDIUM_LOWER_BOUND &&
                            minHeightDp >= HEIGHT_DP_MEDIUM_LOWER_BOUND -> MOBILE_PORTRAIT
                    minWidthDp >= WIDTH_DP_MEDIUM_LOWER_BOUND &&
                            minHeightDp < HEIGHT_DP_MEDIUM_LOWER_BOUND -> MOBILE_LANDSCAPE
                    minWidthDp in WIDTH_DP_MEDIUM_LOWER_BOUND..WIDTH_DP_EXPANDED_LOWER_BOUND &&
                            minHeightDp >= HEIGHT_DP_MEDIUM_LOWER_BOUND -> TABLET_PORTRAIT
                    minWidthDp >= WIDTH_DP_EXPANDED_LOWER_BOUND &&
                            minHeightDp in HEIGHT_DP_MEDIUM_LOWER_BOUND..HEIGHT_DP_EXPANDED_LOWER_BOUND -> TABLET_LANDSCAPE
                    else -> DESKTOP
                }
            }
        }
    }
}