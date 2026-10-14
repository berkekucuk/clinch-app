package com.berkekucuk.mmaapp.core.utils

import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> runSuspendCatching(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend () -> T,
): Result<T> {
    return try {
        Result.success(withContext(context) { block() })
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
