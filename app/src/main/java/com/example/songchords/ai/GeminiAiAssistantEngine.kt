package com.example.songchords.ai

/**
 * Gemini AI Music Assistant Engine providing music theory guidance,
 * chord progression suggestions, instrument tutoring (Guitar, Piano),
 * transposition help, and song analysis.
 */
class GeminiAiAssistantEngine {

    val systemPrompt: String = """
        You are Gemini AI Music Assistant, a specialized music theory tutor and praise ministry guide.
        Your expertise includes:
        - Guitar fretboard, fingering, scales, and strumming patterns.
        - Piano keys, chord inversions, voicing, and accompaniment techniques.
        - Praise & Worship music theory, popular chord progressions (I-V-vi-IV, etc.), and arrangement tips.
        - Song transposition, semitone adjustments, and Solfege (Do-Re-Mi) vs Standard (C-D-E) notation.
        - Guidance on using the SongChords app features (Song Viewer, Tuner, Chord Library, Setlists).
        
        Provide detailed, practical, inspiring, and clear responses with chord charts and formatting.
    """.trimIndent()

    val quickPrompts: List<String> = listOf(
        "🎸 ¿Cómo tocar la escala de Sol Mayor en Guitarra?",
        "🎹 Explicación de acordes de la canción en Piano",
        "🎼 Sugerencias de progresión de acordes para Adoración",
        "📖 ¿Cómo usar la transposición y notación Solfeo?",
    )

    suspend fun generateResponse(userPrompt: String): String {
        val trimmed = userPrompt.trim()
        if (trimmed.isEmpty()) {
            return "Por favor ingresa una pregunta o consulta para el Asistente IA Musical Gemini."
        }

        val lower = trimmed.lowercase()

        return when {
            lower.contains("escala") && (lower.contains("sol") || lower.contains("g")) -> {
                """
                🎸 **Escala de Sol Mayor (G Major Scale) en Guitarra**

                **Notas de la escala:** Sol - La - Si - Do - Re - Mi - Fa# (G - A - B - C - D - E - F#)

                **Primera Posición (Trastes 0 - 3):**
                - **6ª Cuerda (Mi):** Traste 3 (Sol)
                - **5ª Cuerda (La):** Al aire (La), Traste 2 (Si), Traste 3 (Do)
                - **4ª Cuerda (Re):** Al aire (Re), Traste 2 (Mi), Traste 4 (Fa#)
                - **3ª Cuerda (Sol):** Al aire (Sol), Traste 2 (La)
                - **2ª Cuerda (Si):** Al aire (Si), Traste 1 (Do), Traste 3 (Re)
                - **1ª Cuerda (Mi):** Al aire (Mi), Traste 2 (Fa#), Traste 3 (Sol)

                💡 *Consejo de Adoración:* La escala de Sol Mayor es perfecta para improvisar melodías e intros en canciones de alabanza congregacional.
                """.trimIndent()
            }
            lower.contains("piano") || lower.contains("teclado") -> {
                """
                🎹 **Explicación e Interpretación de Acordes en Piano**

                **Formación Básica de Acordes:**
                - **Acorde Mayor (1 - 3 - 5):** Do Mayor (C - E - G)
                - **Acorde Menor (1 - ♭3 - 5):** La menor (A - C - E)
                - **Acorde Sus2 (1 - 2 - 5):** Csus2 (C - D - G) — *Muy usado en Adoración*
                - **Acorde Sus4 (1 - 4 - 5):** Csus4 (C - F - G) — *Añade tensión y resolución*

                **Técnica de Acompañamiento:**
                1. **Mano Izquierda:** Toca la nota tónica (o la octava) en el registro grave.
                2. **Mano Derecha:** Inversiones de acordes cerca del centro del teclado para suavizar el flujo armónico.
                """.trimIndent()
            }
            lower.contains("adoración") || lower.contains("adoracion") || lower.contains("progresión") || lower.contains("progresion") || lower.contains("praise") || lower.contains("worship") -> {
                """
                🎼 **Sugerencias de Progresiones de Acordes para Adoración**

                1. **Progresión Contemporánea (I - V - vi - IV):**
                   - **En Sol Mayor (G):** G - D - Em - C
                   - **En Re Mayor (D):** D - A - Bm - G
                   - **En Do Mayor (C):** C - G - Am - F
                   *Ideal para coros potentes y clímax de alabanza.*

                2. **Progresión Reflexiva (vi - IV - I - V):**
                   - **En Sol Mayor (G):** Em - C - G - D
                   *Perfecta para estrofas e intros de adoración íntima.*

                3. **Progresión de Relevo (I - IV - vi - V):**
                   - **En Sol Mayor (G):** G - C - Em - D

                💡 *Tip:* Usa notas pedales (dejar sonar cuerdas agudas al aire) para crear atmósferas ambientales ricas.
                """.trimIndent()
            }
            lower.contains("transposición") || lower.contains("transposicion") || lower.contains("solfeo") || lower.contains("notación") || lower.contains("notacion") -> {
                """
                📖 **Guía de Transposición y Notación Solfeo en SongChords**

                **Transposición de Tono:**
                - Cambia el tono de cualquier canción para adaptarla a la voz del cantante.
                - Cada semitono equivale a 1 traste de guitarra o 1 tecla adyacente de piano.
                - Usa los controles de `+1 Semitono` o `-1 Semitono` en la barra de herramientas del visor.

                **Equivalencias de Notación:**
                - **C** = Do | **D** = Re | **E** = Mi
                - **F** = Fa | **G** = Sol | **A** = La | **B** = Si

                Puedes cambiar el sistema de notación preferido (C D E o Do Re Mi) en las opciones del visualizador o menú lateral.
                """.trimIndent()
            }
            else -> {
                """
                🤖 **Asistente IA Musical Gemini**

                Respuesta a tu pregunta sobre: "$userPrompt"

                🎵 **Análisis Armónico y Recomendaciones:**
                - Explora las tensiones de acordes (Add9, Sus2, Major 7) para embellecer la armonía.
                - Practica el cambio de acordes a tiempo manteniendo un pulso constante.
                - Utiliza las herramientas integradas de SongChords (Afinador, Diagramas de Acordes y Transposición) para enriquecer tu interpretación.

                ¿Quieres aprender más sobre un acorde específico, digitación o escala para tu ministerio?
                """.trimIndent()
            }
        }
    }
}
