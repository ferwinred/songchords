package com.example.songchords.ui.viewer.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.songchords.model.Chord
import com.example.songchords.model.ChordDiagramProvider
import com.example.songchords.model.NotationSystem
import com.example.songchords.model.Note
import com.example.songchords.model.PianoChordNotes
import com.example.songchords.ui.theme.SongChordsTheme

@Composable
fun PianoChordDiagramView(
    pianoNotes: PianoChordNotes,
    chordName: String,
    modifier: Modifier = Modifier,
    notationSystem: NotationSystem = NotationSystem.STANDARD
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceContainerHighest
    val outlineColor = MaterialTheme.colorScheme.outline
    val textMeasurer = rememberTextMeasurer()

    // 14 White Keys across ~2 octaves: C D E F G A B C D E F G A B
    val whiteKeyPitches = listOf(0, 2, 4, 5, 7, 9, 11, 0, 2, 4, 5, 7, 9, 11)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(12.dp)
    ) {
        Text(
            text = chordName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val totalWidth = size.width
            val canvasHeight = size.height

            val numWhiteKeys = whiteKeyPitches.size
            val whiteKeyWidth = totalWidth / numWhiteKeys
            val whiteKeyHeight = canvasHeight - 24.dp.toPx()

            val blackKeyWidth = whiteKeyWidth * 0.6f
            val blackKeyHeight = whiteKeyHeight * 0.62f

            // 1. Draw White Keys
            for (i in 0 until numWhiteKeys) {
                val pitch = whiteKeyPitches[i]
                val isActive = pianoNotes.pitchClasses.contains(pitch)
                val isRoot = pitch == pianoNotes.rootPitchClass
                val isBass = pianoNotes.bassPitchClass != null && pitch == pianoNotes.bassPitchClass

                val keyX = i * whiteKeyWidth

                val keyColor = when {
                    isRoot -> primaryColor.copy(alpha = 0.35f)
                    isBass -> tertiaryColor.copy(alpha = 0.35f)
                    isActive -> secondaryColor.copy(alpha = 0.35f)
                    else -> surfaceVariant
                }

                drawRoundRect(
                    color = keyColor,
                    topLeft = Offset(keyX, 0f),
                    size = Size(whiteKeyWidth - 2.dp.toPx(), whiteKeyHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                drawRoundRect(
                    color = outlineColor,
                    topLeft = Offset(keyX, 0f),
                    size = Size(whiteKeyWidth - 2.dp.toPx(), whiteKeyHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                )

                // If active, draw circle and note label at bottom of white key
                if (isActive) {
                    val dotColor = when {
                        isRoot -> primaryColor
                        isBass -> tertiaryColor
                        else -> secondaryColor
                    }
                    val circleCenter = Offset(keyX + whiteKeyWidth / 2, whiteKeyHeight - 20.dp.toPx())
                    drawCircle(
                        color = dotColor,
                        radius = 12.dp.toPx(),
                        center = circleCenter
                    )

                    val noteName = Note.fromPitchClass(pitch).toNotationName(notationSystem)
                    val nLayout = textMeasurer.measure(
                        text = noteName,
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = onPrimaryColor)
                    )
                    drawText(
                        textLayoutResult = nLayout,
                        topLeft = Offset(circleCenter.x - nLayout.size.width / 2, circleCenter.y - nLayout.size.height / 2)
                    )
                }
            }

            // 2. Draw Black Keys
            // Pattern of black keys relative to white keys:
            // C(0)->C#(1), D(2)->D#(3), F(5)->F#(6), G(7)->G#(8), A(9)->A#(10)
            val blackKeyOffsetMap = mapOf(
                0 to 1,   // C -> C#
                1 to 3,   // D -> D#
                3 to 6,   // F -> F#
                4 to 8,   // G -> G#
                5 to 10,  // A -> A#
                7 to 1,   // C -> C# (2nd octave)
                8 to 3,   // D -> D#
                10 to 6,  // F -> F#
                11 to 8,  // G -> G#
                12 to 10  // A -> A#
            )

            for ((whiteIndex, blackPitch) in blackKeyOffsetMap) {
                if (whiteIndex >= numWhiteKeys - 1) continue

                val isActive = pianoNotes.pitchClasses.contains(blackPitch)
                val isRoot = blackPitch == pianoNotes.rootPitchClass
                val isBass = pianoNotes.bassPitchClass != null && blackPitch == pianoNotes.bassPitchClass

                val blackKeyX = (whiteIndex + 1) * whiteKeyWidth - blackKeyWidth / 2

                val keyColor = when {
                    isRoot -> primaryColor
                    isBass -> tertiaryColor
                    isActive -> secondaryColor
                    else -> Color.Black
                }

                drawRoundRect(
                    color = keyColor,
                    topLeft = Offset(blackKeyX, 0f),
                    size = Size(blackKeyWidth, blackKeyHeight),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )

                if (isActive) {
                    val noteName = Note.fromPitchClass(blackPitch).toNotationName(notationSystem)
                    val nLayout = textMeasurer.measure(
                        text = noteName,
                        style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = onPrimaryColor)
                    )
                    drawText(
                        textLayoutResult = nLayout,
                        topLeft = Offset(blackKeyX + blackKeyWidth / 2 - nLayout.size.width / 2, blackKeyHeight - 16.dp.toPx())
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PianoChordDiagramViewPreview() {
    SongChordsTheme {
        val chord = Chord.parse("Gadd2")!!
        val pianoNotes = ChordDiagramProvider.getPianoNotes(chord)
        PianoChordDiagramView(
            pianoNotes = pianoNotes,
            chordName = "Gadd2"
        )
    }
}
