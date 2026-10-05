package com.example.songchords.ui.setlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.songchords.R
import com.example.songchords.auth.UserIdentityManager
import com.example.songchords.model.Setlist
import com.example.songchords.model.Song
import com.example.songchords.repository.LocalSetlistRepository
import com.example.songchords.repository.SetlistRepository
import com.example.songchords.subscription.SubscriptionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LimitReachedDialogInfo(
    val titleResId: Int = R.string.limit_reached_title,
    val messageResId: Int,
    val limitValue: Int,
    val currentTierTitleResId: Int
)

class SetlistViewModel(
    private val setlistRepository: SetlistRepository = LocalSetlistRepository()
) : ViewModel() {

    val setlists: StateFlow<List<Setlist>> = setlistRepository.getSetlists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _limitReachedDialog = MutableStateFlow<LimitReachedDialogInfo?>(null)
    val limitReachedDialog: StateFlow<LimitReachedDialogInfo?> = _limitReachedDialog.asStateFlow()

    fun dismissLimitDialog() {
        _limitReachedDialog.value = null
    }

    fun createSetlist(
        title: String,
        description: String? = null,
        userId: String = UserIdentityManager.currentUserId,
        userName: String = UserIdentityManager.currentUserName
    ) {
        val currentCount = setlists.value.size
        if (!SubscriptionManager.canCreateSetlist(currentCount)) {
            val tier = SubscriptionManager.currentTier.value
            _limitReachedDialog.value = LimitReachedDialogInfo(
                messageResId = R.string.limit_setlists_reached_message,
                limitValue = tier.maxSetlists,
                currentTierTitleResId = tier.titleResId
            )
            return
        }

        viewModelScope.launch {
            val newSetlist = Setlist(
                title = title,
                description = description,
                createdByUserId = userId,
                createdByName = userName
            )
            setlistRepository.createSetlist(newSetlist)
        }
    }

    fun deleteSetlist(setlistId: String) {
        viewModelScope.launch {
            setlistRepository.deleteSetlist(setlistId)
        }
    }

    fun addSongToSetlist(setlistId: String, song: Song, transposedKey: String) {
        val targetSetlist = setlists.value.firstOrNull { it.id == setlistId }
        val currentSongsCount = targetSetlist?.songs?.size ?: 0
        if (!SubscriptionManager.canAddSongToSetlist(currentSongsCount)) {
            val tier = SubscriptionManager.currentTier.value
            _limitReachedDialog.value = LimitReachedDialogInfo(
                messageResId = R.string.limit_songs_per_setlist_reached_message,
                limitValue = tier.maxSongsPerSetlist,
                currentTierTitleResId = tier.titleResId
            )
            return
        }

        viewModelScope.launch {
            setlistRepository.addSongToSetlist(setlistId, song, transposedKey)
        }
    }

    fun removeSongFromSetlist(setlistId: String, songId: String) {
        viewModelScope.launch {
            setlistRepository.removeSongFromSetlist(setlistId, songId)
        }
    }

    fun updateSetlistSongKey(setlistId: String, songId: String, newTransposedKey: String) {
        viewModelScope.launch {
            setlistRepository.updateSetlistSongKey(setlistId, songId, newTransposedKey)
        }
    }

    companion object {
        fun Factory(repository: SetlistRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SetlistViewModel(repository) as T
                }
            }
    }
}
