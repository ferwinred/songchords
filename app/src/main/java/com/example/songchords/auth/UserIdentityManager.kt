package com.example.songchords.auth

import android.content.Context
import java.util.UUID

/**
 * Manages user identity (device user ID and display name) for song ownership and permissions.
 * Persists the Cloud Firestore seeding status in SharedPreferences so it skips automatically
 * on subsequent opens (zero duplication).
 */
object UserIdentityManager {

    const val SUPER_ADMIN_EMAIL = "ferwinred@gmail.com"
    const val SUPER_ADMIN_ID = "SUPERADMIN_FERWINRED"
    const val SUPER_ADMIN_NAME = "SuperAdmin (ferwinred@gmail.com)"

    private const val PREFS_NAME = "firestore_prefs"
    private const val KEY_FIRESTORE_SEEDED = "firestore_seeded"

    private var _currentUserId: String = UUID.randomUUID().toString()
    private var _currentUserName: String = "User " + _currentUserId.take(4)
    private var _isFirestoreSeeded: Boolean = false

    var currentUserId: String
        get() = _currentUserId
        set(value) {
            _currentUserId = value
        }

    var currentUserName: String
        get() = _currentUserName
        set(value) {
            _currentUserName = value
        }

    fun isFirestoreSeeded(context: Context? = null): Boolean {
        if (_isFirestoreSeeded) return true
        if (context != null) {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                _isFirestoreSeeded = prefs.getBoolean(KEY_FIRESTORE_SEEDED, false)
            } catch (_: Throwable) {
            }
        }
        return _isFirestoreSeeded
    }

    fun markFirestoreSeeded(context: Context? = null) {
        _isFirestoreSeeded = true
        if (context != null) {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(KEY_FIRESTORE_SEEDED, true).apply()
            } catch (_: Throwable) {
            }
        }
    }

    fun setUserIdentity(userId: String, userName: String) {
        _currentUserId = userId
        _currentUserName = userName
    }

    fun resetToDefault(context: Context? = null) {
        _currentUserId = UUID.randomUUID().toString()
        _currentUserName = "User " + _currentUserId.take(4)
        _isFirestoreSeeded = false
        if (context != null) {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().remove(KEY_FIRESTORE_SEEDED).apply()
            } catch (_: Throwable) {
            }
        }
    }
}
