@file:Suppress("SpreadOperator")
package com.codermp.composeandroidtemplate.core.presentation.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource

/**
 * A sealed interface that represents UI text that can be displayed to the user, either via a string
 * resource or a dynamic string.
 */
@Stable
sealed interface UiText {
    /**
     * A data class that represents a dynamic string, which can be displayed to the user.
     * @param value The string value to display to the user.
     */
    data class Dynamic(
        val value: String
    ): UiText

    /**
     * A data class that represents a string resource, which can be displayed to the user.
     * @param id The resource ID of the string to display to the user.
     * @param args The arguments to format the string with.
     */
    @Stable
    data class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf()
    ): UiText {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StringResource

            if (id != other.id) return false
            if (!args.contentEquals(other = other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id

            result = 31 * result + args.contentHashCode()

            return result
        }
    }

    /**
     * A data class that represents a combined string, which can be displayed to the user.
     * @param format The format string to use to display the string.
     * @param uiTexts The list of [UiText] to display to the user.
     */
    @Stable
    data class Combined(
        val format: String,
        val uiTexts: Array<UiText>
    ): UiText {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Combined

            if (format != other.format) return false
            if (!uiTexts.contentEquals(other = other.uiTexts)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = format.hashCode()
            result = 31 * result + uiTexts.contentHashCode()
            return result
        }
    }

    /**
     * Returns the string representation of this [UiText] based on the provided [Context].
     * @param context The [Context] to use for resolving string resources.
     * @return The string representation of this [UiText].
     */
    fun asString(context: Context): String {
        return when(this) {
            is Dynamic -> value
            is StringResource -> context.getString(id, *args)
            is Combined -> {
                val strings = uiTexts.map { uiText ->
                    when(uiText) {
                        is Combined -> throw IllegalArgumentException("Can't nest combined UiTexts.")
                        is Dynamic -> uiText.value
                        is StringResource -> context.getString(uiText.id, *uiText.args)
                    }
                }
                String.format(format, *strings.toTypedArray())
            }
        }
    }

    /**
     * Returns the string representation of this [UiText] as a [String].
     * @return The string representation of this [UiText].
     */
    @Composable
    fun asString(): String {
        return when(this) {
            is Dynamic -> value
            is StringResource -> stringResource(id = id, *args)
            is Combined -> {
                val strings = uiTexts.map { uiText ->
                    when(uiText) {
                        is Combined -> throw IllegalArgumentException("Can't nest combined UiTexts.")
                        is Dynamic -> uiText.value
                        is StringResource -> stringResource(id = uiText.id, *uiText.args)
                    }
                }
                String.format(format, *strings.toTypedArray())
            }
        }
    }
}