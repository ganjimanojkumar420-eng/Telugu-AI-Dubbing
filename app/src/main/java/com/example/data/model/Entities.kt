package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val durationFormatted: String = "00:00",
    val durationSeconds: Long = 0,
    val fileSizeFormatted: String = "0 MB",
    val sourceLanguage: String = "Auto Detect",
    val targetLanguage: String = "Telugu",
    val voiceMode: String = VoiceMode.AUTO_CHARACTER.name,
    val voiceStyle: String = VoiceStyle.CINEMATIC.name,
    val audioMode: String = AudioMode.FULL_AI_MIX.name,
    val status: String = DubbingStatus.UPLOADED.name,
    val overallProgress: Int = 0,
    val currentChunkIndex: Int = 0,
    val totalChunks: Int = 0,
    val videoUri: String? = null,
    val extractedAudioPath: String? = null,
    val dubbedAudioPath: String? = null,
    val finalVideoPath: String? = null,
    val teluguSrtPath: String? = null,
    val originalSrtPath: String? = null,
    val bilingualSrtPath: String? = null,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audio_chunks")
data class AudioChunkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val chunkIndex: Int,
    val startMs: Long,
    val endMs: Long,
    val durationMs: Long,
    val status: String = ChunkStatus.PENDING.name,
    val retryCount: Int = 0,
    val originalAudioPath: String? = null,
    val transcribedText: String? = null,
    val teluguTranslation: String? = null,
    val generatedVoicePath: String? = null,
    val errorMessage: String? = null
)

@Entity(tableName = "transcript_segments")
data class TranscriptSegmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val chunkIndex: Int,
    val segmentIndex: Int,
    val speakerId: String,
    val speakerName: String,
    val startMs: Long,
    val endMs: Long,
    val originalText: String,
    val teluguText: String,
    val emotion: String = "Cinematic",
    val voiceId: String = "te_male_arjun",
    val pitch: Float = 1.0f,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val generatedAudioPath: String? = null,
    val isEdited: Boolean = false
)

@Entity(tableName = "speaker_profiles")
data class SpeakerProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val speakerCode: String, // e.g. "SPEAKER_01"
    val displayName: String, // e.g. "Protagonist / Hero"
    val gender: String = "Male",
    val assignedVoiceId: String = "te_male_arjun",
    val assignedVoiceName: String = "Telugu Male 1 (Arjun - Cinematic)",
    val pitch: Float = 1.0f,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val emotion: String = "Cinematic"
)

@Entity(tableName = "processing_logs")
data class ProcessingLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val level: String = "INFO", // INFO, WARN, ERROR, SUCCESS
    val step: String,
    val message: String
)

@Entity(tableName = "model_configs")
data class ModelConfigEntity(
    @PrimaryKey val id: Int = 1,
    val sttModel: String = "Whisper Large-v3 (faster-whisper)",
    val translationModel: String = "IndicTrans2 Multilingual Telugu Engine",
    val ttsModel: String = "IndicTTS / MMS-Telugu VITS",
    val modelServerUrl: String = "http://10.0.2.2:8000",
    val ffmpegPath: String = "/usr/bin/ffmpeg",
    val maxChunkDurationSec: Int = 30,
    val parallelWorkers: Int = 4,
    val isOpenSourceMode: Boolean = true,
    val autoDiarization: Boolean = true
)
