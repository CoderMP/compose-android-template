package com.codermp.composeandroidtemplate.core.domain.utils

import com.codermp.composeandroidtemplate.core.domain.utils.Error as MarkerError

typealias EmptyResult<E> = Result<Unit, E>

/**
 * Sealed interface that represents the result of a data operation.
 */
sealed interface Result<out D, out E: MarkerError> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: MarkerError>(val error: E): Result<Nothing, E>
}

/**
 * Inline function to handle success case of the Result.
 */
inline fun <T, E: MarkerError> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Success -> {
            action(data)
            this
        }
        is Result.Error -> this
    }
}

/**
 * Inline function to handle error case of the Result.
 */
inline fun <T, E: MarkerError> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Success -> this
        is Result.Error -> {
            action(error)
            this
        }
    }
}

/**
 * Inline function to map the data of the [Result] to another type [T].
 */
inline fun <T, E: MarkerError, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Success -> Result.Success(map(data))
        is Result.Error -> Result.Error(error)
    }
}

/**
 * Extension function to convert [Result] to an [EmptyResult].
 */
fun <T, E: MarkerError> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  }
}