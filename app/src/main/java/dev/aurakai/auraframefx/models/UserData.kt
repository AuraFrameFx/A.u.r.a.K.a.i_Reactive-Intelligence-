package dev.aurakai.auraframefx.model

/**
 * Represents user profile and authentication data.
 *
 * Used by UserPreferences for storing and retrieving user information,
 * including profile details and API credentials.
 *
 * All fields are nullable to support partial data scenarios and
 * allow users to update individual fields independently.
 */
data class UserData(
    /**
     * Unique user identifier (UUID or database ID).
     */
    val id: String? = null,

    /**
     * User's display name or full name.
     */
    val name: String? = null,

    /**
     * User's email address for account identification and communication.
     */
    val email: String? = null,

    /**
     * API key for authenticated requests to AI services (Vertex AI, etc.).
     */
    val apiKey: String? = null,

    /**
     * User's avatar image URI or URL.
     */
    val avatarUrl: String? = null,

    /**
     * User preferences and settings JSON blob.
     */
    val preferences: String? = null,

    /**
     * Account creation timestamp (milliseconds since epoch).
     */
    val createdAt: Long? = null,

    /**
     * Last updated timestamp (milliseconds since epoch).
     */
    val updatedAt: Long? = null
)
