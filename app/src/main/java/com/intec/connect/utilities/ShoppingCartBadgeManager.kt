package com.intec.connect.utilities

class ShoppingCartBadgeManager private constructor(private val initialCount: Int = 0) {

    private var badgeCount = initialCount

    fun incrementBadgeCount() {
        badgeCount++
    }

    fun decrementBadgeCount() {
        if (badgeCount > 0) {
            badgeCount--
        }
    }

    fun setBadgeCount(count: Int) {
        badgeCount = count
    }

    fun getBadgeCount(): Int {
        return badgeCount
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