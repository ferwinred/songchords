package com.example.songchords.ui.setlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.songchords.R
import com.example.songchords.model.Setlist
import com.example.songchords.model.Song

private val COMMON_KEYS = listOf(
    "C", "C#", "Db", "D", "Eb", "E", "F", "F#", "Gb", "G", "Ab", "A", "Bb", "B",
    "Am", "Bm", "Cm", "Dm", "Em", "Fm", "Gm"
)

@Composable
fun CreateSetlistDialog(
    onDismissRequest: () -> Unit,
    onCreateSetlist: (title: String, description: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(R.string.create_setlist),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        showError = false
                    },
                    label = { Text(stringResource(R.string.setlist_title_label)) },
                    isError = showError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Text(
                        text = stringResource(R.string.error_enter_title),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.setlist_description_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        onCreateSetlist(title.trim(), description.trim().ifBlank { null })
                        onDismissRequest()
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddToSetlistDialog(
    song: Song,
    setlists: List<Setlist>,
    onDismissRequest: () -> Unit,
    onConfirm: (setlistId: String, selectedKey: String) -> Unit,
    onCreateNewSetlist: (() -> Unit)? = null
) {
    var selectedSetlistId by remember(setlists) { mutableStateOf(setlists.firstOrNull()?.id ?: "") }
    var transposedKey by remember(song) { mutableStateOf(song.originalKey) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(R.string.add_to_setlist),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "${song.title} • ${song.artist}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.select_setlist),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (setlists.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_setlists_found),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (onCreateNewSetlist != null) {
                        OutlinedButton(
                            onClick = {
                                onDismissRequest()
                                onCreateNewSetlist()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.create_setlist))
                        }
                    }
                } else {
                    setlists.forEach { setlist ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSetlistId = setlist.id },
                            shape = MaterialTheme.shapes.small,
                            color = if (selectedSetlistId == setlist.id) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedSetlistId == setlist.id,
                                    onClick = { selectedSetlistId = setlist.id }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = setlist.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    val countText = if (setlist.songs.size == 1) {
                                        stringResource(R.string.songs_count_one, 1)
                                    } else {
                                        stringResource(R.string.songs_count_other, setlist.songs.size)
                                    }
                                    Text(
                                        text = countText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.transposed_key_override),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.original_key_format, song.originalKey),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = transposedKey,
                    onValueChange = { transposedKey = it },
                    label = { Text(stringResource(R.string.setlist_key)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    COMMON_KEYS.forEach { keyOption ->
                        FilterChip(
                            selected = transposedKey.equals(keyOption, ignoreCase = true),
                            onClick = { transposedKey = keyOption },
                            label = { Text(keyOption, style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = if (transposedKey.equals(keyOption, ignoreCase = true)) {
                                { Icon(Icons.Rounded.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = selectedSetlistId.isNotBlank(),
                onClick = {
                    onConfirm(selectedSetlistId, transposedKey.trim().ifBlank { song.originalKey })
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.save_transposed_key))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UpdateSongKeyDialog(
    songTitle: String,
    originalKey: String,
    currentTransposedKey: String,
    onDismissRequest: () -> Unit,
    onConfirm: (newTransposedKey: String) -> Unit
) {
    var transposedKey by remember { mutableStateOf(currentTransposedKey) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(R.string.edit_setlist_key),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = songTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.original_key_format, originalKey),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = transposedKey,
                    onValueChange = { transposedKey = it },
                    label = { Text(stringResource(R.string.setlist_key)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    COMMON_KEYS.forEach { keyOption ->
                        FilterChip(
                            selected = transposedKey.equals(keyOption, ignoreCase = true),
                            onClick = { transposedKey = keyOption },
                            label = { Text(keyOption, style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = if (transposedKey.equals(keyOption, ignoreCase = true)) {
                                { Icon(Icons.Rounded.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(transposedKey.trim().ifBlank { originalKey })
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun PlanLimitReachedDialog(
    title: String = stringResource(R.string.limit_reached_title),
    message: String,
    onDismissRequest: () -> Unit,
    onOpenSubscription: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onOpenSubscription()
                }
            ) {
                Text(
                    text = stringResource(R.string.view_plans),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.understood))
            }
        }
    )
}
