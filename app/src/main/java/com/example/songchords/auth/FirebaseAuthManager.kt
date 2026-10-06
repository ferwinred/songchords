package com.example.songchords.auth

import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Encapsulates user auth state (uid, email, displayName, isAnonymous).
 */
data class FirebaseUserState(
    val uid: String = UserIdentityManager.currentUserId,
    val email: String? = null,
    val displayName: String? = UserIdentityManager.currentUserName,
    val isAnonymous: Boolean = true,
    val isSignedIn: Boolean = false,
)

/**
 * Manages Firebase Authentication operations and exposes active user credentials.
 */
@Suppress("unused")
object FirebaseAuthManager {

    private val _authState = MutableStateFlow(
        FirebaseUserState(
            uid = UserIdentityManager.currentUserId,
            email = null,
            displayName = UserIdentityManager.currentUserName,
            isAnonymous = true,
            isSignedIn = false
        )
    )
    val authState: StateFlow<FirebaseUserState> = _authState.asStateFlow()

    val uid: String
        get() = _authState.value.uid

    val email: String?
        get() = _authState.value.email

    val displayName: String?
        get() = _authState.value.displayName

    val isAnonymous: Boolean
        get() = _authState.value.isAnonymous

    val isSignedIn: Boolean
        get() = _authState.value.isSignedIn

    private var firebaseAuth: FirebaseAuth? = null

    init {
        try {
            firebaseAuth = FirebaseAuth.getInstance()
            updateStateFromFirebase()
            firebaseAuth?.addAuthStateListener {
                updateStateFromFirebase()
            }
        } catch (_: Exception) {
            // Firebase Auth not initialized in unit tests / headless environment
        }
    }

    private fun updateStateFromFirebase() {
        val user = firebaseAuth?.currentUser
        if (user != null) {
            val state = FirebaseUserState(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName ?: user.email?.substringBefore("@") ?: "User ${user.uid.take(4)}",
                isAnonymous = user.isAnonymous,
                isSignedIn = true
            )
            _authState.value = state
            UserIdentityManager.setUserIdentity(state.uid, state.displayName ?: "User")
            if (!state.isAnonymous && (SubscriptionManager.currentTier.value == UserTier.ANONYMOUS)) {
                SubscriptionManager.setTier(UserTier.REGISTERED_FREE)
            }
        } else {
            _authState.value = FirebaseUserState(
                uid = UserIdentityManager.currentUserId,
                email = null,
                displayName = UserIdentityManager.currentUserName,
                isAnonymous = true,
                isSignedIn = false
            )
        }
    }

    /**
     * Signs in anonymously for guest users.
     */
    suspend fun signInAnonymously(): Result<FirebaseUserState> {
        return try {
            val auth = firebaseAuth
            if (auth != null) {
                val authResult = auth.signInAnonymously().await()
                val user = authResult.user
                val newState = FirebaseUserState(
                    uid = user?.uid ?: "anon_${System.currentTimeMillis()}",
                    email = null,
                    displayName = "Guest User",
                    isAnonymous = true,
                    isSignedIn = true
                )
                _authState.value = newState
                UserIdentityManager.setUserIdentity(newState.uid, newState.displayName ?: "Guest User")
                if (SubscriptionManager.currentTier.value != UserTier.ANONYMOUS) {
                    SubscriptionManager.setTier(UserTier.ANONYMOUS)
                }
                Result.success(newState)
            } else {
                val fallbackUid = "anon_test_${System.currentTimeMillis()}"
                val newState = FirebaseUserState(
                    uid = fallbackUid,
                    email = null,
                    displayName = "Guest User",
                    isAnonymous = true,
                    isSignedIn = true
                )
                _authState.value = newState
                UserIdentityManager.setUserIdentity(newState.uid, newState.displayName ?: "Guest User")
                if (SubscriptionManager.currentTier.value != UserTier.ANONYMOUS) {
                    SubscriptionManager.setTier(UserTier.ANONYMOUS)
                }
                Result.success(newState)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs in with Google using ID Token.
     */
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUserState> {
        return try {
            if (idToken.isBlank()) {
                return Result.failure(IllegalArgumentException("ID Token cannot be empty"))
            }
            val auth = firebaseAuth
            if (auth != null) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user
                val newState = FirebaseUserState(
                    uid = user?.uid ?: "google_${System.currentTimeMillis()}",
                    email = user?.email ?: "googleuser@example.com",
                    displayName = user?.displayName ?: user?.email?.substringBefore("@") ?: "Google User",
                    isAnonymous = false,
                    isSignedIn = true
                )
                _authState.value = newState
                UserIdentityManager.setUserIdentity(newState.uid, newState.displayName ?: "Google User")
                if (SubscriptionManager.currentTier.value == UserTier.ANONYMOUS) {
                    SubscriptionManager.setTier(UserTier.REGISTERED_FREE)
                }
                Result.success(newState)
            } else {
                val fallbackUid = "google_user_${idToken.hashCode()}"
                val newState = FirebaseUserState(
                    uid = fallbackUid,
                    email = "user@gmail.com",
                    displayName = "Google User",
                    isAnonymous = false,
                    isSignedIn = true
                )
                _authState.value = newState
                UserIdentityManager.setUserIdentity(newState.uid, newState.displayName ?: "Google User")
                if (SubscriptionManager.currentTier.value == UserTier.ANONYMOUS) {
                    SubscriptionManager.setTier(UserTier.REGISTERED_FREE)
                }
                Result.success(newState)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (_: Exception) {
        }
        _authState.value = FirebaseUserState()
        UserIdentityManager.resetToDefault()
        SubscriptionManager.resetToDefault()
    }

    fun setMockUserState(state: FirebaseUserState) {
        _authState.value = state
        UserIdentityManager.setUserIdentity(state.uid, state.displayName ?: "Test User")
    }

    fun resetToDefault() {
        _authState.value = FirebaseUserState()
    }
}
