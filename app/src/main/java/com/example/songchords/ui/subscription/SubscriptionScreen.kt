package com.example.songchords.ui.subscription

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.songchords.firestore.FirestoreSeeder
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.songchords.R
import com.example.songchords.subscription.UserTier
import com.example.songchords.ui.theme.DarkNavyBlue
import com.example.songchords.ui.theme.SongChordsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubscriptionViewModel = viewModel(),
) {
    val currentTier by viewModel.currentTier.collectAsStateWithLifecycle()
    val displayPlans = viewModel.displayPlans
    val context = LocalContext.current

    var showPromoDialog by remember { mutableStateOf(false) }
    var promoCodeInput by remember { mutableStateOf("") }
    var promoErrorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.subscription_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkNavyBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WorkspacePremium,
                        contentDescription = null,
                        modifier = Modifier
                            .height(48.dp)
                            .width(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.subscription_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = {
                        promoCodeInput = ""
                        promoErrorMessage = null
                        showPromoDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CardGiftcard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.redeem_promo_code_button),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            item {
                val coroutineScope = rememberCoroutineScope()
                var isSeeding by remember { mutableStateOf(false) }

                Button(
                    onClick = {
                        if (isSeeding) return@Button
                        isSeeding = true
                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                "🔥 Iniciando siembra de Cloud Firestore (131 documentos)...",
                                Toast.LENGTH_SHORT
                            ).show()
                            val report = FirestoreSeeder.seedAllCollections(context = context)
                            isSeeding = false
                            if (report.successfulCollections == report.totalCollections) {
                                Toast.makeText(
                                    context,
                                    "✅ Base de Datos Firestore sembrada con éxito (${report.totalDocumentsSeeded} documentos en ${report.totalCollections} colecciones)",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    enabled = !isSeeding,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
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
                        Text("Sembrando Firestore...")
                    } else {
                        Text(
                            text = "🔥 Sembrar Base de Datos Firestore",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            items(displayPlans) { tier ->
                PlanCard(
                    tier = tier,
                    isCurrent = (tier == currentTier),
                    onSelect = {
                        viewModel.selectTier(tier)
                        val planName = context.getString(tier.titleResId)
                        val toastMsg = context.getString(R.string.plan_activated_toast, planName)
                        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    if (showPromoDialog) {
        AlertDialog(
            onDismissRequest = { showPromoDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.redeem_promo_code_dialog_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.subscription_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = promoCodeInput,
                        onValueChange = {
                            promoCodeInput = it
                            promoErrorMessage = null
                        },
                        label = { Text(stringResource(R.string.promo_code_input_label)) },
                        placeholder = { Text(stringResource(R.string.promo_code_placeholder)) },
                        singleLine = true,
                        isError = promoErrorMessage != null,
                        supportingText = promoErrorMessage?.let { errorText ->
                            { Text(text = errorText, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (promoCodeInput.isBlank()) {
                            promoErrorMessage = context.getString(R.string.promo_code_empty_error)
                            return@Button
                        }
                        viewModel.redeemPromoCode(promoCodeInput) { result ->
                            result.fold(
                                onSuccess = {
                                    showPromoDialog = false
                                    val successMessage = context.getString(R.string.promo_code_success)
                                    Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
                                },
                                onFailure = { exception ->
                                    promoErrorMessage = exception.message ?: context.getString(R.string.promo_code_invalid)
                                }
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.redeem_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showPromoDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun PlanCard(
    tier: UserTier,
    isCurrent: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBorder = if (isCurrent) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    }

    val cardContainerColor = if (isCurrent) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = cardBorder,
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrent) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(tier.titleResId),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (tier == UserTier.REGISTERED_FREE) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.requires_google_sign_in),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(tier.priceResId),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (isCurrent) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(16.dp)
                                    .width(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.current_plan_badge),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Feature Checklist
            val setlistsText = if (tier.maxSetlists == Int.MAX_VALUE) {
                stringResource(R.string.feature_setlists_unlimited)
            } else {
                stringResource(R.string.feature_setlists_limit, tier.maxSetlists)
            }
            PlanFeatureRow(
                enabled = tier.maxSetlists > 0,
                text = setlistsText
            )

            val songsPerSetlistText = if (tier.maxSongsPerSetlist == Int.MAX_VALUE) {
                stringResource(R.string.feature_songs_per_setlist_unlimited)
            } else {
                stringResource(R.string.feature_songs_per_setlist_limit, tier.maxSongsPerSetlist)
            }
            PlanFeatureRow(
                enabled = tier.maxSongsPerSetlist > 0,
                text = songsPerSetlistText
            )

            val customSongsText = if (tier.maxCustomSongs == Int.MAX_VALUE) {
                stringResource(R.string.feature_custom_songs_unlimited)
            } else {
                stringResource(R.string.feature_custom_songs_limit, tier.maxCustomSongs)
            }
            PlanFeatureRow(
                enabled = tier.maxCustomSongs > 0,
                text = customSongsText
            )

            PlanFeatureRow(
                enabled = tier.isAdFree,
                text = stringResource(if (tier.isAdFree) R.string.feature_ad_free else R.string.feature_with_ads)
            )

            PlanFeatureRow(
                enabled = tier.hasAiAssistant,
                text = stringResource(if (tier.hasAiAssistant) R.string.feature_ai_assistant else R.string.feature_no_ai_assistant)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isCurrent) {
                OutlinedButton(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = stringResource(R.string.current_plan_badge))
                }
            } else {
                Button(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (tier == UserTier.REGISTERED_FREE) {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier
                                .height(20.dp)
                                .width(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.sign_in_with_google),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.select_plan_button),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlanFeatureRow(
    enabled: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (enabled) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .height(18.dp)
                    .width(18.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                modifier = Modifier
                    .height(18.dp)
                    .width(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionScreenPreview() {
    SongChordsTheme {
        SubscriptionScreen(onNavigateBack = {})
    }
}
