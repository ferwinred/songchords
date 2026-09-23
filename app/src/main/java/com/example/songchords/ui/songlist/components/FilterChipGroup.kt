package com.example.songchords.ui.songlist.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.songchords.R
import com.example.songchords.ui.theme.SongChordsTheme

@Composable
fun FilterChipGroup(
    showOnlyFavorites: Boolean,
    selectedKeyFilter: String?,
    selectedArtistFilter: String?,
    selectedTagFilter: String?,
    availableKeys: List<String>,
    availableArtists: List<String>,
    availableTags: List<String>,
    isAnyFilterActive: Boolean,
    onToggleFavoritesFilter: () -> Unit,
    onKeyFilterSelect: (String?) -> Unit,
    onArtistFilterSelect: (String?) -> Unit,
    onTagFilterSelect: (String?) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Clear all filters chip (if active)
        if (isAnyFilterActive) {
            InputChip(
                selected = true,
                onClick = onClearFilters,
                label = { Text(stringResource(R.string.clear_all_filters)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = stringResource(R.string.clear_all_filters_desc)
                    )
                },
                colors = InputChipDefaults.inputChipColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.onErrorContainer,
                    trailingIconColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }

        // Favorites Filter Chip
        FilterChip(
            selected = showOnlyFavorites,
            onClick = onToggleFavoritesFilter,
            label = { Text(stringResource(R.string.filter_favorites)) },
            leadingIcon = {
                Icon(
                    imageVector = if (showOnlyFavorites) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = stringResource(R.string.filter_favorites_desc),
                    tint = if (showOnlyFavorites) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        // Key Filter Dropdown Chip
        FilterDropdownChip(
            label = stringResource(R.string.filter_key),
            allLabel = stringResource(R.string.all_keys),
            selectedValue = selectedKeyFilter,
            options = availableKeys,
            icon = Icons.Rounded.MusicNote,
            onOptionSelect = onKeyFilterSelect
        )

        // Artist Filter Dropdown Chip
        FilterDropdownChip(
            label = stringResource(R.string.filter_artist),
            allLabel = stringResource(R.string.all_artists),
            selectedValue = selectedArtistFilter,
            options = availableArtists,
            icon = Icons.Rounded.Person,
            onOptionSelect = onArtistFilterSelect
        )

        // Tag Filter Dropdown Chip
        FilterDropdownChip(
            label = stringResource(R.string.filter_tag),
            allLabel = stringResource(R.string.all_tags),
            selectedValue = selectedTagFilter,
            options = availableTags,
            icon = Icons.AutoMirrored.Rounded.Label,
            onOptionSelect = onTagFilterSelect
        )
    }
}

@Composable
private fun FilterDropdownChip(
    label: String,
    allLabel: String,
    selectedValue: String?,
    options: List<String>,
    icon: ImageVector,
    onOptionSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FilterChip(
            selected = selectedValue != null,
            onClick = { expanded = true },
            label = {
                Text(
                    text = if (selectedValue != null) stringResource(R.string.filter_label_format, label, selectedValue) else label
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.filter_icon_desc, label)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = stringResource(R.string.expand_menu_desc, label)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(allLabel) },
                onClick = {
                    onOptionSelect(null)
                    expanded = false
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipGroupPreview() {
    SongChordsTheme {
        FilterChipGroup(
            showOnlyFavorites = false,
            selectedKeyFilter = "G",
            selectedArtistFilter = null,
            selectedTagFilter = null,
            availableKeys = listOf("G", "D", "A", "E"),
            availableArtists = listOf("Alex Campos", "Marcos Witt"),
            availableTags = listOf("Alabanza", "Adoración", "Worship"),
            isAnyFilterActive = true,
            onToggleFavoritesFilter = {},
            onKeyFilterSelect = {},
            onArtistFilterSelect = {},
            onTagFilterSelect = {},
            onClearFilters = {}
        )
    }
}
