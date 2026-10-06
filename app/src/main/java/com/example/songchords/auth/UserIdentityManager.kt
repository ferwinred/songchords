package com.example.songchords.auth

import java.util.UUID

/**
 * Manages user identity (device user ID and display name) for song ownership and permissions.
 */
object UserIdentityManager {

    const val SUPER_ADMIN_EMAIL = "ferwinred@gmail.com"
    const val SUPER_ADMIN_ID = "SUPERADMIN_FERWINRED"
    const val SUPER_ADMIN_NAME = "SuperAdmin (ferwinred@gmail.com)"

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

    fun isFirestoreSeeded(): Boolean = _isFirestoreSeeded

    fun markFirestoreSeeded() {
        _isFirestoreSeeded = true
    }

    fun setUserIdentity(userId: String, userName: String) {
        _currentUserId = userId
        _currentUserName = userName
    }

    fun resetToDefault() {
        _currentUserId = UUID.randomUUID().toString()
        _currentUserName = "User " + _currentUserId.take(4)
        _isFirestoreSeeded = false
    }
}
