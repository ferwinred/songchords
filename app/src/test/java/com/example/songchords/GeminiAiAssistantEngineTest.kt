package com.example.songchords

import com.example.songchords.ai.GeminiAiAssistantEngine
import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import com.example.songchords.ui.ai.AiAssistantViewModel
import com.example.songchords.ui.ai.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GeminiAiAssistantEngineTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var engine: GeminiAiAssistantEngine

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        SubscriptionManager.resetToDefault()
        engine = GeminiAiAssistantEngine()
    }

    @After
    fun tearDown() {
        SubscriptionManager.resetToDefault()
        Dispatchers.resetMain()
    }

    @Test
    fun testSystemPromptTunedForWorshipAndInstruments() {
        val prompt = engine.systemPrompt
        assertTrue(prompt.contains("Guitar"))
        assertTrue(prompt.contains("Piano"))
        assertTrue(prompt.contains("Praise & Worship"))
        assertTrue(prompt.contains("chord progressions"))
        assertTrue(prompt.contains("transposition"))
    }

    @Test
    fun testQuickPromptsAvailability() {
        val prompts = engine.quickPrompts
        assertEquals(4, prompts.size)
        assertTrue(prompts[0].contains("escala de Sol Mayor"))
        assertTrue(prompts[1].contains("acordes de la canción en Piano"))
        assertTrue(prompts[2].contains("progresión de acordes para Adoración"))
        assertTrue(prompts[3].contains("transposición y notación Solfeo"))
    }

    @Test
    fun testGenerateResponseForGuitarScale() = runBlocking {
        val response = engine.generateResponse("🎸 ¿Cómo tocar la escala de Sol Mayor en Guitarra?")
        assertTrue(response.contains("Escala de Sol Mayor"))
        assertTrue(response.contains("Sol - La - Si - Do - Re - Mi - Fa#"))
        assertTrue(response.contains("Traste 3"))
    }

    @Test
    fun testGenerateResponseForPianoChords() = runBlocking {
        val response = engine.generateResponse("🎹 Explicación de acordes de la canción en Piano")
        assertTrue(response.contains("Piano"))
        assertTrue(response.contains("Acorde Mayor"))
        assertTrue(response.contains("Acorde Sus2"))
    }

    @Test
    fun testGenerateResponseForWorshipProgression() = runBlocking {
        val response = engine.generateResponse("🎼 Sugerencias de progresión de acordes para Adoración")
        assertTrue(response.contains("Progresiones de Acordes para Adoración"))
        assertTrue(response.contains("G - D - Em - C"))
    }

    @Test
    fun testGenerateResponseForTranspositionSolfege() = runBlocking {
        val response = engine.generateResponse("📖 ¿Cómo usar la transposición y notación Solfeo?")
        assertTrue(response.contains("Transposición de Tono"))
        assertTrue(response.contains("Do"))
        assertTrue(response.contains("Re"))
    }

    @Test
    fun testGenerateResponseForCustomPrompt() = runBlocking {
        val response = engine.generateResponse("¿Cómo afinar la guitarra en Drop D?")
        assertTrue(response.contains("Asistente IA Musical Gemini"))
        assertTrue(response.contains("Drop D"))
    }

    @Test
    fun testGenerateResponseForEmptyPrompt() = runBlocking {
        val response = engine.generateResponse("   ")
        assertTrue(response.contains("Por favor ingresa una pregunta"))
    }

    @Test
    fun testViewModelGatingAndMessaging() = runBlocking {
        val tierFlow = MutableStateFlow(UserTier.REGISTERED_FREE)
        val viewModel = AiAssistantViewModel(engine, tierFlow)

        // Initially REGISTERED_FREE -> hasAccess should be false
        var state = viewModel.uiState.value
        assertFalse(state.hasAccess)

        // Attempt send without access -> messages remain empty
        viewModel.onInputChange("Hola AI")
        viewModel.sendMessage()
        assertEquals(0, viewModel.uiState.value.messages.size)

        // Upgrade user to PREMIUM_AI_SUB
        tierFlow.value = UserTier.PREMIUM_AI_SUB

        state = viewModel.uiState.value
        assertTrue(state.hasAccess)

        // Send a message with Premium AI tier
        viewModel.onInputChange("🎸 ¿Cómo tocar la escala de Sol Mayor en Guitarra?")
        viewModel.sendMessage()

        val finalMessages = viewModel.uiState.value.messages
        assertEquals(2, finalMessages.size)
        assertEquals(MessageSender.USER, finalMessages[0].sender)
        assertEquals(MessageSender.ASSISTANT, finalMessages[1].sender)
        assertTrue(finalMessages[1].text.contains("Escala de Sol Mayor"))
    }
}
