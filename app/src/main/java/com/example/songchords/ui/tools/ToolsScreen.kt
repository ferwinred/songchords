package com.example.songchords.ui.tools

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.songchords.R
import com.example.songchords.firestore.ProductionFirestoreInitializer
import com.example.songchords.repository.SyncStatus
import com.example.songchords.ui.chordlibrary.ChordLibraryScreen
import com.example.songchords.ui.components.TwoTierHeader
import com.example.songchords.ui.theme.SongChordsTheme
import com.example.songchords.ui.tuner.TunerScreen
import kotlinx.coroutines.launch

enum class ToolsTab {
    TUNER,
    CHORD_LIBRARY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: ToolsTab = ToolsTab.TUNER,
    onOpenSubscription: () -> Unit = {},
    syncStatus: SyncStatus = SyncStatus.IDLE,
    onCloudSyncClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(initialTab.ordinal) }

    val importJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { _ -> }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TwoTierHeader(
                titleText = stringResource(R.string.app_name),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                },
                syncStatus = syncStatus,
                onCloudSyncClick = onCloudSyncClick,
                onSubscriptionClick = onOpenSubscription,
                onImportJsonClick = {
                    importJsonLauncher.launch(arrayOf("application/json", "*/*"))
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            var isSeeding by remember { mutableStateOf(false) }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Button(
                    onClick = {
                        if (isSeeding) return@Button
                        isSeeding = true
                        coroutineScope.launch {
                            ProductionFirestoreInitializer.initializeAll(context = context)
                            isSeeding = false
                        }
                    },
                    enabled = !isSeeding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    if (isSeeding) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onTertiary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Inicializando Firestore...")
                    } else {
                        Text(
                            text = stringResource(R.string.init_production_firestore),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            PrimaryTabRow(
                selectedTabIndex = selectedTab
            ) {
                Tab(
                    selected = selectedTab == ToolsTab.TUNER.ordinal,
                    onClick = { selectedTab = ToolsTab.TUNER.ordinal },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_tuner),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                Tab(
                    selected = selectedTab == ToolsTab.CHORD_LIBRARY.ordinal,
                    onClick = { selectedTab = ToolsTab.CHORD_LIBRARY.ordinal },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_chord_library),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            when (selectedTab) {
                ToolsTab.TUNER.ordinal -> TunerScreen()
                ToolsTab.CHORD_LIBRARY.ordinal -> ChordLibraryScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToolsScreenPreview() {
    SongChordsTheme {
        ToolsScreen(onNavigateBack = {})
    }
}
