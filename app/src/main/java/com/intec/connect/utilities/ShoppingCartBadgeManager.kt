package com.intec.connect.utilities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ShoppingCartBadgeManager private constructor(initialCount: Int = 0) {

    private val _badgeCount = MutableLiveData(initialCount)
    val badgeCount: LiveData<Int> = _badgeCount

    fun incrementBadgeCount() {
        _badgeCount.value = (_badgeCount.value ?: 0) + 1
    }

    fun decrementBadgeCount() {
        val currentCount = _badgeCount.value ?: 0
        if (currentCount > 0) {
            _badgeCount.value = currentCount - 1
        }
    }

    fun setBadgeCount(count: Int) {
        _badgeCount.value = count
    }

    companion object {
        @Volatile
        private var instance: ShoppingCartBadgeManager? = null

        fun getInstance(initialCount: Int = 0): ShoppingCartBadgeManager {
            return instance ?: synchronized(this) {
                instance ?: ShoppingCartBadgeManager(initialCount).also { instance = it }
            }
        }
    }
}