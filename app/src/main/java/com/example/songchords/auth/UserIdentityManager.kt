package com.example.songchords.auth

import java.util.UUID

/**
 * Manages user identity (device user ID and display name) for song ownership and permissions.
 */
object UserIdentityManager {

    private var _currentUserId: String = UUID.randomUUID().toString()
    private var _currentUserName: String = "User " + _currentUserId.take(4)

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

    fun setUserIdentity(userId: String, userName: String) {
        _currentUserId = userId
        _currentUserName = userName
    }

    fun resetToDefault() {
        _currentUserId = UUID.randomUUID().toString()
        _currentUserName = "User " + _currentUserId.take(4)
    }
}
