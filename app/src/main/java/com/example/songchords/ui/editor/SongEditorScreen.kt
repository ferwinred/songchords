package com.example.songchords.ui.editor

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Comment
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.songchords.R
import com.example.songchords.ui.components.LanguageSelector
import com.example.songchords.ui.songlist.components.SongDetailPane

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SongEditorScreen(
    viewModel: SongEditorViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var customChordDialogText by remember { mutableStateOf("") }
    var showCustomChordDialog by remember { mutableStateOf(false) }

    val allKeys = listOf(
        "C", "C#", "Db", "D", "Eb", "E", "F", "F#", "Gb", "G", "Ab", "A", "Bb", "B",
        "Am", "Bm", "Cm", "Dm", "Em", "Fm", "Gm", "F#m", "C#m"
    )

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val context = LocalContext.current
    val importJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importFromJsonUri(context, it) { success ->
                val message = if (success) {
                    context.getString(R.string.import_json_success, viewModel.uiState.value.title)
                } else {
                    context.getString(R.string.import_json_error)
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isNewSong) stringResource(R.string.create_custom_song) else stringResource(R.string.edit_custom_song),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { importJsonLauncher.launch(arrayOf("application/json", "*/*")) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FileOpen,
                            contentDescription = stringResource(R.string.import_json_desc),
                            tint = Color.White
                        )
                    }
                    LanguageSelector(tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 12.dp),
                            color = Color.White
                        )
                    } else {
                        IconButton(
                            onClick = viewModel::saveSong
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = stringResource(R.string.save),
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Segmented Mode Switcher / Tab Row
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                SegmentedButton(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.onSelectTab(0) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text(
                        text = stringResource(R.string.tab_write_lyrics),
                        fontWeight = FontWeight.Bold
                    )
                }
                SegmentedButton(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.onSelectTab(1) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text(
                        text = stringResource(R.string.tab_live_preview_segmented),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            when (uiState.selectedTab) {
                0 -> {
                    // Editor Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Card 1: Song Metadata (Información General)
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Section Header
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.section_general_info),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Row 1: Title Field (full width)
                                OutlinedTextField(
                                    value = uiState.title,
                                    onValueChange = viewModel::onTitleChange,
                                    label = { Text(stringResource(R.string.label_song_title) + " *") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.MusicNote,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Row 2: Artist Field (full width)
                                OutlinedTextField(
                                    value = uiState.artist,
                                    onValueChange = viewModel::onArtistChange,
                                    label = { Text(stringResource(R.string.label_artist)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Row 3: Key & Time Signature (2-column Row)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    var keyDropdownExpanded by remember { mutableStateOf(false) }

                                    ExposedDropdownMenuBox(
                                        expanded = keyDropdownExpanded,
                                        onExpandedChange = { keyDropdownExpanded = it },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        OutlinedTextField(
                                            value = uiState.originalKey,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text(stringResource(R.string.label_key)) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Rounded.Key,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = keyDropdownExpanded) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                            singleLine = true
                                        )

                                        ExposedDropdownMenu(
                                            expanded = keyDropdownExpanded,
                                            onDismissRequest = { keyDropdownExpanded = false }
                                        ) {
                                            allKeys.forEach { keyOption ->
                                                DropdownMenuItem(
                                                    text = { Text(keyOption, fontWeight = FontWeight.Bold) },
                                                    onClick = {
                                                        viewModel.onKeyChange(keyOption)
                                                        keyDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = uiState.timeSignature,
                                        onValueChange = viewModel::onTimeSignatureChange,
                                        label = { Text(stringResource(R.string.label_time_signature)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.Schedule,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Row 4: Tempo (BPM) & Notas / Comentarios (2-column Row)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = uiState.tempoText,
                                        onValueChange = viewModel::onTempoChange,
                                        label = { Text(stringResource(R.string.label_tempo)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Rounded.Speed,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = uiState.comments,
                                        onValueChange = viewModel::onCommentsChange,
                                        label = { Text(stringResource(R.string.comment_label)) },
                                        placeholder = { Text(stringResource(R.string.comment_hint)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.Comment,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Row 5: Etiquetas / Tags (full width)
                                OutlinedTextField(
                                    value = uiState.tagsText,
                                    onValueChange = viewModel::onTagsChange,
                                    label = { Text(stringResource(R.string.label_tags)) },
                                    placeholder = { Text(stringResource(R.string.placeholder_tags)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.Tag,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Card 2: Spacious Editor Container (Letra y Acordes)
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Section Header
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.EditNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.section_lyrics_chords),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Text(
                                    text = stringResource(R.string.lyrics_chords_instruction),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Docked Floating Quick Helper Toolbar & Smart Autocomplete Bar
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    tonalElevation = 4.dp,
                                    shadowElevation = 2.dp,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // 1. Smart Autocomplete Row (Visible when typing '[' or '#')
                                        val autocompleteState = uiState.autocompleteState
                                        AnimatedVisibility(
                                            visible = autocompleteState != null,
                                            enter = fadeIn() + expandVertically(),
                                            exit = fadeOut() + shrinkVertically()
                                        ) {
                                            autocompleteState?.let { state ->
                                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.FlashOn,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Text(
                                                            text = if (state.isBracket) stringResource(R.string.autocomplete_bracket_title) else stringResource(R.string.autocomplete_comment_title),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }

                                                    LazyRow(
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        items(state.suggestions) { suggestion ->
                                                            FilterChip(
                                                                selected = true,
                                                                onClick = { viewModel.applyAutocompleteSuggestion(suggestion) },
                                                                label = {
                                                                    Text(
                                                                        text = suggestion.label,
                                                                        style = MaterialTheme.typography.labelMedium,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                },
                                                                colors = FilterChipDefaults.filterChipColors(
                                                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // 2. Docked Herramientas Rápidas Toolbar
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.FlashOn,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = stringResource(R.string.section_quick_tools),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // # Comentario Button
                                            item {
                                                AssistChip(
                                                    onClick = { viewModel.insertCommentPrefix() },
                                                    label = {
                                                        Text(
                                                            text = "# Comentario",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Rounded.Comment,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    },
                                                    colors = AssistChipDefaults.assistChipColors(
                                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                        labelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                                        leadingIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                                    )
                                                )
                                            }

                                            // Section Chips: [Intro], [Estrofa], [Coro], [Puente], [Final]
                                            items(listOf("Intro", "Estrofa", "Coro", "Puente", "Final")) { section ->
                                                AssistChip(
                                                    onClick = { viewModel.insertSectionHeader(section) },
                                                    label = {
                                                        Text(
                                                            text = "[$section]",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                )
                                            }

                                            // Key Chord Chips: [G], [Am], [C], [D], [Em]...
                                            items(uiState.quickChordsForKey) { chord ->
                                                AssistChip(
                                                    onClick = { viewModel.insertChordTag(chord) },
                                                    label = {
                                                        Text(
                                                            text = "[$chord]",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    },
                                                    colors = AssistChipDefaults.assistChipColors(
                                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                )
                                            }

                                            // + Acorde Button
                                            item {
                                                AssistChip(
                                                    onClick = { showCustomChordDialog = true },
                                                    label = {
                                                        Text(
                                                            text = stringResource(R.string.add_chord),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Rounded.Add,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = uiState.contentTextFieldValue,
                                    onValueChange = viewModel::onContentValueChange,
                                    label = { Text(stringResource(R.string.label_lyrics_chord_tags)) },
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                    minLines = 16,
                                    maxLines = 40,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Live Preview Tab Content
                    val previewSong = uiState.parsedSongPreview.song
                    SongDetailPane(
                        song = previewSong,
                        onToggleFavorite = {},
                        showHeader = false
                    )
                }
            }
        }
    }

    // Custom Chord Insert Dialog
    if (showCustomChordDialog) {
        AlertDialog(
            onDismissRequest = { showCustomChordDialog = false },
            title = { Text(stringResource(R.string.insert_custom_chord_title)) },
            text = {
                OutlinedTextField(
                    value = customChordDialogText,
                    onValueChange = { customChordDialogText = it },
                    label = { Text(stringResource(R.string.label_chord_symbol)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customChordDialogText.isNotBlank()) {
                            viewModel.insertChordTag(customChordDialogText.trim())
                            customChordDialogText = ""
                            showCustomChordDialog = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.insert))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomChordDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
