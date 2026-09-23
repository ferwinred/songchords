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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.songchords.model.Chord
import com.example.songchords.model.ChordDiagramProvider
import com.example.songchords.model.GuitarChordDiagram
import com.example.songchords.ui.theme.SongChordsTheme

@Composable
fun GuitarChordDiagramView(
    diagram: GuitarChordDiagram,
    chordName: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val outlineColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface
    val textMeasurer = rememberTextMeasurer()

    val stringNames = listOf("E", "A", "D", "G", "B", "e")

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

        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val width = size.width
            val height = size.height

            val leftMargin = 32.dp.toPx()
            val rightMargin = 20.dp.toPx()
            val topMargin = 36.dp.toPx()
            val bottomMargin = 28.dp.toPx()

            val gridWidth = width - leftMargin - rightMargin
            val gridHeight = height - topMargin - bottomMargin

            val numStrings = 6
            val numFrets = 4

            val stringSpacing = gridWidth / (numStrings - 1)
            val fretSpacing = gridHeight / numFrets

            // 1. Draw Nut or Top Fret Line
            if (diagram.baseFret == 1) {
                drawRect(
                    color = textColor,
                    topLeft = Offset(leftMargin, topMargin - 4.dp.toPx()),
                    size = Size(gridWidth, 6.dp.toPx())
                )
            } else {
                drawLine(
                    color = textColor,
                    start = Offset(leftMargin, topMargin),
                    end = Offset(leftMargin + gridWidth, topMargin),
                    strokeWidth = 2.dp.toPx()
                )
                // Draw Base Fret Indicator on left side
                val fretText = "${diagram.baseFret}fr"
                val textLayout = textMeasurer.measure(
                    text = fretText,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(8.dp.toPx(), topMargin + fretSpacing / 2 - textLayout.size.height / 2)
                )
            }

            // 2. Draw Fret Horizontal Lines
            for (f in 1..numFrets) {
                val y = topMargin + f * fretSpacing
                drawLine(
                    color = outlineColor.copy(alpha = 0.6f),
                    start = Offset(leftMargin, y),
                    end = Offset(leftMargin + gridWidth, y),
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // 3. Draw Strings Vertical Lines and Names below
            for (s in 0 until numStrings) {
                val x = leftMargin + s * stringSpacing
                drawLine(
                    color = textColor.copy(alpha = 0.8f),
                    start = Offset(x, topMargin),
                    end = Offset(x, topMargin + gridHeight),
                    strokeWidth = (1.2 + (5 - s) * 0.3).dp.toPx() // Thicker low strings
                )

                // Draw string names at bottom
                val sName = stringNames[s]
                val sLayout = textMeasurer.measure(
                    text = sName,
                    style = TextStyle(fontSize = 11.sp, color = textColor.copy(alpha = 0.7f))
                )
                drawText(
                    textLayoutResult = sLayout,
                    topLeft = Offset(x - sLayout.size.width / 2, topMargin + gridHeight + 6.dp.toPx())
                )
            }

            // 4. Draw Above Nut Symbols (X and O)
            for (s in 0 until numStrings) {
                val x = leftMargin + s * stringSpacing
                val fretVal = diagram.frets.getOrElse(s) { 0 }

                if (fretVal == -1) {
                    // Draw 'X'
                    val xLayout = textMeasurer.measure(
                        text = "✕",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Red.copy(alpha = 0.8f))
                    )
                    drawText(
                        textLayoutResult = xLayout,
                        topLeft = Offset(x - xLayout.size.width / 2, topMargin - 26.dp.toPx())
                    )
                } else if (fretVal == 0) {
                    // Draw 'O'
                    val oLayout = textMeasurer.measure(
                        text = "○",
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    )
                    drawText(
                        textLayoutResult = oLayout,
                        topLeft = Offset(x - oLayout.size.width / 2, topMargin - 26.dp.toPx())
                    )
                }
            }

            // 5. Draw Finger Dots on Frets
            val dotRadius = 11.dp.toPx()

            for (s in 0 until numStrings) {
                val x = leftMargin + s * stringSpacing
                val fretVal = diagram.frets.getOrElse(s) { -1 }

                if (fretVal > 0) {
                    val relFret = fretVal - diagram.baseFret + 1
                    if (relFret in 1..numFrets) {
                        val y = topMargin + (relFret - 0.5f) * fretSpacing

                        // Circle Dot
                        drawCircle(
                            color = primaryColor,
                            radius = dotRadius,
                            center = Offset(x, y)
                        )

                        // Finger number if provided
                        val fingerVal = diagram.fingers.getOrElse(s) { 0 }
                        if (fingerVal > 0) {
                            val fLayout = textMeasurer.measure(
                                text = fingerVal.toString(),
                                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = onPrimaryColor)
                            )
                            drawText(
                                textLayoutResult = fLayout,
                                topLeft = Offset(x - fLayout.size.width / 2, y - fLayout.size.height / 2)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GuitarChordDiagramViewPreview() {
    SongChordsTheme {
        val chord = Chord.parse("Cadd2")!!
        val diagram = ChordDiagramProvider.getGuitarDiagram(chord)
        GuitarChordDiagramView(
            diagram = diagram,
            chordName = "Cadd2"
        )
    }
}
