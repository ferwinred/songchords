package com.example.songchords.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.songchords.R
import com.example.songchords.repository.SyncStatus

@Composable
fun CloudSyncButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    syncStatus: SyncStatus = SyncStatus.IDLE,
    tint: Color = Color.White
) {
    val syncIcon = when (syncStatus) {
        SyncStatus.SYNCING -> Icons.Rounded.CloudSync
        SyncStatus.OFFLINE_ERROR -> Icons.Rounded.CloudOff
        SyncStatus.SYNCED, SyncStatus.IDLE -> Icons.Rounded.CloudDone
    }
    val syncDesc = when (syncStatus) {
        SyncStatus.SYNCING -> stringResource(R.string.cloud_syncing)
        SyncStatus.OFFLINE_ERROR -> stringResource(R.string.cloud_offline)
        SyncStatus.SYNCED, SyncStatus.IDLE -> stringResource(R.string.cloud_synced)
    }

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = syncIcon,
            contentDescription = syncDesc,
            tint = if (syncStatus == SyncStatus.OFFLINE_ERROR) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                tint
            }
        )
    }
}
