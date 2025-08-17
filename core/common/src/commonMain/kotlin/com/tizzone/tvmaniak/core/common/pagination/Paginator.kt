package com.tizzone.tvmaniak.core.common.pagination

import com.tizzone.tvmaniak.core.common.utils.Result

class Paginator<Key, Item>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> Result<Item>,
    private val getNextKey: suspend (currentKey: Key, result: Item) -> Key,
    private val onError: suspend (Throwable?) -> Unit,
    private val onSuccess: suspend (result: Item, newKey: Key) -> Unit,
    private val endReached: (currentKey: Key, result: Item) -> Boolean,
) {
    private var currentKey = initialKey
    private var isMakingRequest = false
    private var isEndReached = false

    suspend fun loadNextItems() {
        if (isMakingRequest || isEndReached) {
            return
        }

        isMakingRequest = true
        onLoadUpdated(true)

        val result = onRequest(currentKey)
        isMakingRequest = false

        when (result) {
            is Result.Success -> {
                val item = result.data
                val newKey = getNextKey(currentKey, item)
                val endReachedResult = endReached(currentKey, item)
                currentKey = newKey
                onSuccess(item, currentKey)
                onLoadUpdated(false)
                isEndReached = endReachedResult
            }

            is Result.Error -> {
                println("Paginator.loadNextItems - Error: ${result.exception?.message}")
                onError(result.exception)
                onLoadUpdated(false)
                return
            }
        }
    }

    fun reset() {
        currentKey = initialKey
        isEndReached = false
    }
}
