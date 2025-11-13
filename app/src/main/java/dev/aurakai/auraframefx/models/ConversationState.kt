package dev.aurakai.auraframefx.model

/**
 * Represents the state of a voice conversation or chat interaction.
 *
 * Used by NeuralWhisper and other conversational AI components to track
 * the current phase of user interaction.
 *
 * States:
 * - Idle: No conversation in progress
 * - Listening: Actively listening for user input (voice or text)
 * - Recording: Recording audio from the user
 * - Processing: Transcribing or understanding user input
 * - Responding: AI is formulating or delivering a response
 * - Speaking: AI is delivering a voice response
 * - Error: Conversation encountered an error
 */
sealed class ConversationState {
    /**
     * No active conversation - waiting for user input.
     */
    data object Idle : ConversationState()

    /**
     * Actively listening for user voice input.
     */
    data object Listening : ConversationState()

    /**
     * AI is delivering a verbal response to the user.
     */
    data object Speaking : ConversationState()

    /**
     * Recording audio input from the user.
     */
    data object Recording : ConversationState()

    /**
     * Processing and transcribing user input.
     *
     * @param partialTranscript Partial transcription results as they become available
     */
    data class Processing(val partialTranscript: String? = null) : ConversationState()

    /**
     * AI is generating or delivering a response.
     *
     * @param responseText The response text being generated or delivered
     */
    data class Responding(val responseText: String? = null) : ConversationState()

    /**
     * Conversation encountered an error.
     *
     * @param errorMessage Description of the error that occurred
     */
    data class Error(val errorMessage: String) : ConversationState()
}
