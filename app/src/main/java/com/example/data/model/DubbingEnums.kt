package com.example.data.model

enum class DubbingStatus(val label: String, val stepNumber: Int) {
    UPLOADED("Uploaded", 1),
    EXTRACTING_AUDIO("Extracting Audio", 2),
    TRANSCRIBING("Speech Recognition", 3),
    DETECTING_SPEAKERS("Speaker Detection", 4),
    TRANSLATING("Telugu Translation", 5),
    GENERATING_VOICE("Telugu Voice Generation", 6),
    SYNCING_AUDIO("Audio Synchronization", 7),
    RENDERING("Final Video Rendering", 8),
    COMPLETED("Dubbing Completed", 9),
    FAILED("Processing Failed", 0),
    PAUSED("Paused", 0);

    val isTerminal: Boolean
        get() = this == COMPLETED || this == FAILED

    val isRunning: Boolean
        get() = !isTerminal && this != PAUSED && this != UPLOADED
}

enum class ChunkStatus(val label: String) {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    SKIPPED("Skipped")
}

enum class VoiceMode(val label: String, val description: String) {
    AUTO_CHARACTER("Auto Character Voices", "Automatically detects male, female, and characters"),
    SINGLE_MALE("Single Male Voice", "All dialogue rendered in deep cinematic Telugu male voice"),
    SINGLE_FEMALE("Single Female Voice", "All dialogue rendered in melodic Telugu female voice"),
    CUSTOM_CHARACTER("Custom Character Voices", "Manually assign individual voice profiles per speaker")
}

enum class VoiceStyle(val label: String) {
    NATURAL("Natural"),
    CINEMATIC("Cinematic"),
    EMOTIONAL("Emotional"),
    CALM("Calm"),
    DRAMATIC("Dramatic")
}

enum class AudioMode(val label: String, val description: String) {
    REPLACE_DIALOGUE("Replace Dialogue Only", "Duck original dialogue volume by 95% and overlay Telugu voice"),
    PRESERVE_MUSIC("Preserve Background Music", "Retain background score while replacing vocal track"),
    PRESERVE_SFX("Preserve Sound Effects", "Preserve gunshots, explosions, and foley ambience"),
    FULL_AI_MIX("Full AI Mix", "Intelligent balanced multi-channel audio mixing with stereo output")
}

enum class SupportedLanguage(val code: String, val displayName: String) {
    AUTO("auto", "Auto Detect"),
    ENGLISH("en", "English"),
    HINDI("hi", "Hindi"),
    TAMIL("ta", "Tamil"),
    TELUGU("te", "Telugu"),
    SPANISH("es", "Spanish"),
    FRENCH("fr", "French"),
    KOREAN("ko", "Korean"),
    JAPANESE("ja", "Japanese")
}

data class TeluguVoicePreset(
    val id: String,
    val name: String,
    val gender: String,
    val tone: String,
    val pitch: Float = 1.0f,
    val speed: Float = 1.0f
)
