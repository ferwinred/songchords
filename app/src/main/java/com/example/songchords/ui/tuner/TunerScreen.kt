package com.example.songchords.ui.tuner

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.songchords.R
import com.example.songchords.ui.theme.SongChordsTheme
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TunerScreen(
    modifier: Modifier = Modifier
) {
    var selectedInstrument by remember { mutableStateOf(InstrumentType.GUITAR_6) }
    val stringList = remember(selectedInstrument) {
        TunerEngine.getStringsForInstrument(selectedInstrument)
    }
    var selectedStringIndex by remember(selectedInstrument) { mutableStateOf(0) }

    val activeTargetString = stringList.getOrElse(selectedStringIndex) { stringList.first() }

    var simulatedCentsOffset by remember { mutableDoubleStateOf(0.0) }
    val actualFrequency = TunerEngine.frequencyFromCents(activeTargetString.frequencyHz, simulatedCentsOffset)
    val status = TunerEngine.getTuningStatus(simulatedCentsOffset)

    val toneGenerator = remember { AudioToneGenerator() }
    var isPlayingTone by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            toneGenerator.stopTone()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.tuner_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Instrument Selector Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            InstrumentType.entries.forEach { instrument ->
                val isSelected = instrument == selectedInstrument
                ElevatedFilterChip(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            selectedInstrument = instrument
                            selectedStringIndex = 0
                            simulatedCentsOffset = 0.0
                            if (isPlayingTone) {
                                toneGenerator.stopTone()
                                isPlayingTone = false
                            }
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(instrument.titleRes),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target String Chips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Seleccionar Cuerda / Target String",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    stringList.forEachIndexed { index, targetString ->
                        val isSelected = index == selectedStringIndex
                        ElevatedFilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedStringIndex = index
                                simulatedCentsOffset = 0.0
                                if (isPlayingTone) {
                                    toneGenerator.stopTone()
                                    isPlayingTone = false
                                }
                            },
                            label = {
                                Text(
                                    text = "${targetString.noteName} (${targetString.frequencyHz} Hz)",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${targetString.number}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Canvas Pitch Gauge Meter
        TunerPitchGaugeMeter(
            targetString = activeTargetString,
            actualFrequencyHz = actualFrequency,
            centsOffset = simulatedCentsOffset,
            status = status,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Play Sound Tone Generator Button
        Button(
            onClick = {
                if (isPlayingTone) {
                    toneGenerator.stopTone()
                    isPlayingTone = false
                } else {
                    isPlayingTone = true
                    toneGenerator.playTone(
                        scope = coroutineScope,
                        frequencyHz = activeTargetString.frequencyHz,
                        durationMs = 3000L,
                        onComplete = { isPlayingTone = false }
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPlayingTone) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (isPlayingTone) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSecondaryContainer
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Icon(
                imageVector = if (isPlayingTone) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isPlayingTone) stringResource(R.string.stop_sound) else stringResource(R.string.play_sound),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Manual Pitch Adjustment Controls for testing gauge needle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.pitch_adjust_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Slider(
                    value = simulatedCentsOffset.toFloat(),
                    onValueChange = { simulatedCentsOffset = it.toDouble() },
                    valueRange = -50f..50f,
                    steps = 100,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = { simulatedCentsOffset = (simulatedCentsOffset - 5.0).coerceAtLeast(-50.0) }) {
                        Text("-5c")
                    }
                    OutlinedButton(onClick = { simulatedCentsOffset = (simulatedCentsOffset - 1.0).coerceAtLeast(-50.0) }) {
                        Text("-1c")
                    }
                    Button(onClick = { simulatedCentsOffset = 0.0 }) {
                        Text("Reset (0c)")
                    }
                    OutlinedButton(onClick = { simulatedCentsOffset = (simulatedCentsOffset + 1.0).coerceAtMost(50.0) }) {
                        Text("+1c")
                    }
                    OutlinedButton(onClick = { simulatedCentsOffset = (simulatedCentsOffset + 5.0).coerceAtMost(50.0) }) {
                        Text("+5c")
                    }
                }
            }
        }
    }
}

@Composable
fun TunerPitchGaugeMeter(
    targetString: TargetString,
    actualFrequencyHz: Double,
    centsOffset: Double,
    status: TuningStatus,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    val statusColor by animateColorAsState(
        targetValue = when (status) {
            TuningStatus.IN_TUNE -> Color(0xFF2E7D32) // Green
            TuningStatus.FLAT -> Color(0xFFED6C02) // Orange
            TuningStatus.SHARP -> Color(0xFFD32F2F) // Red
        },
        animationSpec = tween(300),
        label = "statusColor"
    )

    val animatedCents by animateFloatAsState(
        targetValue = centsOffset.toFloat().coerceIn(-50f, 50f),
        animationSpec = tween(200),
        label = "centsOffset"
    )

    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Header Badge
            Surface(
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(statusColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (status) {
                            TuningStatus.IN_TUNE -> stringResource(R.string.status_in_tune)
                            TuningStatus.FLAT -> stringResource(R.string.status_flat)
                            TuningStatus.SHARP -> stringResource(R.string.status_sharp)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            // Canvas Gauge Dial
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val centerX = canvasWidth / 2f
                val centerY = canvasHeight - 20.dp.toPx()
                val radius = (canvasWidth / 2f - 30.dp.toPx()).coerceAtMost(canvasHeight - 30.dp.toPx())

                // Draw Arc Background Meter (-60 deg to +60 deg relative to top)
                val startAngle = 210f // 210 degrees = -60 from top
                val sweepAngle = 120f  // 120 degrees sweep (-60 to +60)

                drawArc(
                    color = outlineColor.copy(alpha = 0.3f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw Center In-Tune Zone (-5 to +5 cents = -6 to +6 degrees)
                val inTuneStartAngle = 270f - 6f
                drawArc(
                    color = Color(0xFF2E7D32).copy(alpha = 0.6f),
                    startAngle = inTuneStartAngle,
                    sweepAngle = 12f,
                    useCenter = false,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )

                // Scale Ticks and Labels: -50, -25, 0, +25, +50 cents
                val tickCents = listOf(-50, -25, 0, 25, 50)
                tickCents.forEach { cents ->
                    val angleDeg = 270f + (cents / 50f) * 60f
                    val angleRad = Math.toRadians(angleDeg.toDouble())

                    val innerR = radius - 16.dp.toPx()
                    val outerR = radius + 8.dp.toPx()

                    val startPt = Offset(
                        (centerX + innerR * cos(angleRad)).toFloat(),
                        (centerY + innerR * sin(angleRad)).toFloat()
                    )
                    val endPt = Offset(
                        (centerX + outerR * cos(angleRad)).toFloat(),
                        (centerY + outerR * sin(angleRad)).toFloat()
                    )

                    drawLine(
                        color = if (cents == 0) Color(0xFF2E7D32) else onSurfaceColor.copy(alpha = 0.6f),
                        start = startPt,
                        end = endPt,
                        strokeWidth = if (cents == 0) 3.5.dp.toPx() else 2.dp.toPx()
                    )

                    // Draw scale text label
                    val labelR = radius - 28.dp.toPx()
                    val labelX = (centerX + labelR * cos(angleRad)).toFloat()
                    val labelY = (centerY + labelR * sin(angleRad)).toFloat()

                    val labelStr = if (cents > 0) "+$cents" else "$cents"
                    val textLayout = textMeasurer.measure(
                        text = labelStr,
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor.copy(alpha = 0.7f))
                    )
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(labelX - textLayout.size.width / 2, labelY - textLayout.size.height / 2)
                    )
                }

                // Needle Line pointing based on animatedCents (-50 to +50 -> -60 deg to +60 deg)
                val needleAngleDeg = (animatedCents / 50f) * 60f
                val needleAngleRad = Math.toRadians((270f + needleAngleDeg).toDouble())

                val needleLength = radius - 10.dp.toPx()
                val needleEnd = Offset(
                    (centerX + needleLength * cos(needleAngleRad)).toFloat(),
                    (centerY + needleLength * sin(needleAngleRad)).toFloat()
                )

                // Draw Needle
                drawLine(
                    color = statusColor,
                    start = Offset(centerX, centerY),
                    end = needleEnd,
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Draw Center Base Circle
                drawCircle(
                    color = statusColor,
                    radius = 10.dp.toPx(),
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = surfaceColor,
                    radius = 4.dp.toPx(),
                    center = Offset(centerX, centerY)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Note Name Display
            Text(
                text = targetString.noteName,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = statusColor
            )

            // Cents Offset Text
            Text(
                text = stringResource(R.string.cents_format, centsOffset),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Frequencies Info Row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text(
                    text = stringResource(R.string.target_frequency, targetString.frequencyHz),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.detected_frequency, actualFrequencyHz),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TunerScreenPreview() {
    SongChordsTheme {
        TunerScreen()
    }
}
