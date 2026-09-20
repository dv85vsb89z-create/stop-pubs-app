package com.blockpubs.app

import android.content.Context

class ProtectionManager(context: Context) {

    private val prefs = context.getSharedPreferences("protection_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_EXPIRES_AT = "expires_at"
        private const val DURATION_MS = 30L * 60L * 1000L
    }

    fun activateForThirtyMinutes() {
        val expiresAt = System.currentTimeMillis() + DURATION_MS
        prefs.edit().putLong(KEY_EXPIRES_AT, expiresAt).apply()
    }

    fun isActive(): Boolean {
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)
        return System.currentTimeMillis() < expiresAt
    }

    fun remainingTimeMillis(): Long {
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)
        return (expiresAt - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun clear() {
        prefs.edit().remove(KEY_EXPIRES_AT).apply()
    }
}
