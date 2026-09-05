package com.example.ai

import android.content.Context
import com.example.data.model.AudioChunkEntity
import com.example.data.model.AudioMode
import com.example.data.model.ChunkStatus
import com.example.data.model.DubbingStatus
import com.example.data.model.ProjectEntity
import com.example.data.model.SpeakerProfileEntity
import com.example.data.model.TranscriptSegmentEntity
import com.example.data.repository.DubbingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DubbingPipelineEngine(
    private val context: Context,
    private val repository: DubbingRepository,
    private val sttProvider: SpeechToTextProvider,
    private val translationProvider: TranslationProvider,
    private val ttsProvider: TextToSpeechProvider,
    private val audioProcessor: AudioProcessingProvider
) {

    private val engineScope = CoroutineScope(Dispatchers.Default + Job())
    private var activeJob: Job? = null

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _activeProjectId = MutableStateFlow<String?>(null)
    val activeProjectId: StateFlow<String?> = _activeProjectId.asStateFlow()

    fun startPipeline(projectId: String) {
        if (_isProcessing.value && _activeProjectId.value == projectId) return

        activeJob?.cancel()
        _isProcessing.value = true
        _activeProjectId.value = projectId

        activeJob = engineScope.launch {
            try {
                runDubbingWorkflow(projectId)
            } catch (e: CancellationException) {
                repository.addLog(projectId, "Pipeline", "Processing paused or cancelled by user.", "WARN")
                repository.updateProjectStatus(projectId, DubbingStatus.PAUSED.name)
            } catch (e: Exception) {
                repository.addLog(projectId, "Pipeline", "Fatal pipeline error: ${e.localizedMessage}", "ERROR")
                repository.updateProjectStatus(projectId, DubbingStatus.FAILED.name)
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun pausePipeline(projectId: String) {
        if (_activeProjectId.value == projectId) {
            activeJob?.cancel()
            _isProcessing.value = false
            engineScope.launch {
                repository.addLog(projectId, "Pipeline", "Processing paused by user.", "INFO")
                repository.updateProjectStatus(projectId, DubbingStatus.PAUSED.name)
            }
        }
    }

    suspend fun retryChunk(projectId: String, chunkId: Long) = withContext(Dispatchers.IO) {
        repository.addLog(projectId, "Retry", "Retrying chunk #$chunkId...", "INFO")
        repository.updateChunkStatus(chunkId, ChunkStatus.PENDING.name, null)
        startPipeline(projectId)
    }

    suspend fun skipChunk(projectId: String, chunkId: Long) = withContext(Dispatchers.IO) {
        repository.addLog(projectId, "Skip", "Skipping chunk #$chunkId by user command.", "WARN")
        repository.updateChunkStatus(chunkId, ChunkStatus.SKIPPED.name, "Skipped by user")
        startPipeline(projectId)
    }

    suspend fun retryAllFailed(projectId: String) = withContext(Dispatchers.IO) {
        val failed = repository.getFailedChunks(projectId)
        repository.addLog(projectId, "Retry", "Resetting ${failed.size} failed chunks to PENDING.", "INFO")
        failed.forEach {
            repository.updateChunkStatus(it.id, ChunkStatus.PENDING.name, null)
        }
        startPipeline(projectId)
    }

    private suspend fun runDubbingWorkflow(projectId: String) {
        val project = repository.getProjectOnce(projectId) ?: return
        val filesDir = File(context.filesDir, "projects/$projectId")
        filesDir.mkdirs()

        repository.addLog(projectId, "Workflow", "Initializing pipeline for '${project.title}' (${project.fileSizeFormatted})", "INFO")

        // Step 1 & 2: Extract & Chunk Audio
        var chunks = repository.getChunksOnce(projectId)
        if (chunks.isEmpty()) {
            repository.updateProgress(projectId, DubbingStatus.EXTRACTING_AUDIO.name, 10, 0)
            repository.addLog(projectId, "FFmpeg", "Extracting original 48kHz audio track from video container...", "INFO")

            val audioOutPath = File(filesDir, "original_audio.wav").absolutePath
            audioProcessor.extractAudioFromVideo(project.videoUri ?: "sample_movie.mp4", audioOutPath)

            repository.updateProgress(projectId, DubbingStatus.EXTRACTING_AUDIO.name, 18, 0)
            repository.addLog(projectId, "Chunking", "Splitting audio stream into 30-second synchronized chunks...", "INFO")

            val chunkInfosResult = audioProcessor.splitAudioIntoChunks(
                audioPath = audioOutPath,
                chunkDurationSec = 30,
                totalDurationMs = project.durationSeconds * 1000L,
                outputDir = File(filesDir, "chunks").absolutePath
            )

            val chunkInfos = chunkInfosResult.getOrDefault(emptyList())
            val chunkEntities = chunkInfos.map { c ->
                AudioChunkEntity(
                    projectId = projectId,
                    chunkIndex = c.chunkIndex,
                    startMs = c.startMs,
                    endMs = c.endMs,
                    durationMs = c.durationMs,
                    status = ChunkStatus.PENDING.name,
                    originalAudioPath = c.audioChunkPath
                )
            }
            repository.insertChunks(chunkEntities)
            repository.saveProject(project.copy(totalChunks = chunkEntities.size))
            chunks = repository.getChunksOnce(projectId)
        }

        // Initialize Speaker Profiles if not already created
        var speakers = repository.getSpeakersOnce(projectId)
        if (speakers.isEmpty()) {
            val initialSpeakers = listOf(
                SpeakerProfileEntity(
                    projectId = projectId,
                    speakerCode = "SPEAKER_01",
                    displayName = "Protagonist (Hero)",
                    gender = "Male",
                    assignedVoiceId = "te_male_arjun",
                    assignedVoiceName = "Telugu Male 1 (Arjun - Heroic)",
                    pitch = 1.0f,
                    speed = 1.0f,
                    volume = 1.0f,
                    emotion = "Cinematic"
                ),
                SpeakerProfileEntity(
                    projectId = projectId,
                    speakerCode = "SPEAKER_02",
                    displayName = "Allied Commander / Heroine",
                    gender = "Female",
                    assignedVoiceId = "te_female_priya",
                    assignedVoiceName = "Telugu Female 1 (Priya - Expressive)",
                    pitch = 1.05f,
                    speed = 0.98f,
                    volume = 1.0f,
                    emotion = "Emotional"
                ),
                SpeakerProfileEntity(
                    projectId = projectId,
                    speakerCode = "SPEAKER_03",
                    displayName = "Antagonist / Commander",
                    gender = "Male",
                    assignedVoiceId = "te_male_vikram",
                    assignedVoiceName = "Telugu Male 2 (Vikram - Intense Bass)",
                    pitch = 0.92f,
                    speed = 1.02f,
                    volume = 1.05f,
                    emotion = "Dramatic"
                )
            )
            repository.insertSpeakers(initialSpeakers)
            speakers = repository.getSpeakersOnce(projectId)
        }

        val totalChunks = chunks.size
        repository.addLog(projectId, "Pipeline", "Total chunks to process: $totalChunks. Executing STT, Translation & Voice synthesis...", "INFO")

        // Step 3, 4, 5, 6: Process each chunk independently
        for ((idx, chunk) in chunks.withIndex()) {
            if (chunk.status == ChunkStatus.COMPLETED.name || chunk.status == ChunkStatus.SKIPPED.name) {
                continue
            }

            val chunkNum = chunk.chunkIndex + 1
            repository.updateChunkStatus(chunk.id, ChunkStatus.PROCESSING.name, null)

            // Calculate progress percentage: 20% to 75% distributed across chunks
            val chunkProgressBase = 20 + ((idx * 55) / totalChunks)
            repository.updateProgress(projectId, DubbingStatus.TRANSCRIBING.name, chunkProgressBase, chunkNum)
            repository.addLog(projectId, "Whisper", "Transcribing dialogue in chunk $chunkNum/$totalChunks (${chunk.startMs / 1000}s - ${chunk.endMs / 1000}s)...", "INFO")

            // Transcribe chunk
            val sttResult = sttProvider.transcribeChunk(
                projectId = projectId,
                chunkIndex = chunk.chunkIndex,
                audioPath = chunk.originalAudioPath ?: "",
                sourceLanguage = project.sourceLanguage,
                startOffsetMs = chunk.startMs,
                chunkDurationMs = chunk.durationMs
            )

            if (sttResult.isFailure) {
                val err = sttResult.exceptionOrNull()?.localizedMessage ?: "STT failure"
                repository.updateChunkStatus(chunk.id, ChunkStatus.FAILED.name, err)
                repository.addLog(projectId, "Whisper", "Chunk $chunkNum failed: $err", "ERROR")
                continue // Do not kill entire movie! Continue with next chunks.
            }

            val segments = sttResult.getOrDefault(emptyList()).toMutableList()

            // Step 5: Natural Telugu Translation for segments
            repository.updateProgress(projectId, DubbingStatus.TRANSLATING.name, chunkProgressBase + 2, chunkNum)
            repository.addLog(projectId, "Translator", "Translating chunk $chunkNum dialogue into natural conversational Telugu...", "INFO")

            val translatedSegments = mutableListOf<TranscriptSegmentEntity>()
            for (seg in segments) {
                val transRes = translationProvider.translateToTelugu(
                    originalText = seg.originalText,
                    sourceLanguage = project.sourceLanguage,
                    emotion = seg.emotion,
                    context = "Cinematic context: ${seg.speakerName}"
                )
                val teluguText = transRes.getOrDefault(seg.teluguText)

                // Match speaker profile settings if available
                val speakerProfile = speakers.find { it.speakerCode == seg.speakerId }
                val voiceId = speakerProfile?.assignedVoiceId ?: seg.voiceId
                val pitch = speakerProfile?.pitch ?: seg.pitch
                val speed = speakerProfile?.speed ?: seg.speed

                translatedSegments.add(
                    seg.copy(
                        teluguText = teluguText,
                        voiceId = voiceId,
                        pitch = pitch,
                        speed = speed
                    )
                )
            }
            repository.insertSegments(translatedSegments)

            // Step 6: Generate Telugu Voice
            repository.updateProgress(projectId, DubbingStatus.GENERATING_VOICE.name, chunkProgressBase + 4, chunkNum)
            repository.addLog(projectId, "TTS", "Synthesizing Telugu character voices for chunk $chunkNum with IndicTTS...", "INFO")

            val chunkVoiceFile = File(filesDir, "voice_chunk_${chunk.chunkIndex}.wav").absolutePath
            val ttsRes = ttsProvider.synthesizeVoice(
                text = translatedSegments.joinToString(" ") { it.teluguText },
                voiceId = "te_male_arjun",
                pitch = 1.0f,
                speed = 1.0f,
                volume = 1.0f,
                outputFilePath = chunkVoiceFile
            )

            if (ttsRes.isSuccess) {
                repository.updateChunk(
                    chunk.copy(
                        status = ChunkStatus.COMPLETED.name,
                        transcribedText = translatedSegments.joinToString(" ") { it.originalText },
                        teluguTranslation = translatedSegments.joinToString(" ") { it.teluguText },
                        generatedVoicePath = chunkVoiceFile
                    )
                )
                repository.addLog(projectId, "Chunking", "Chunk $chunkNum/$totalChunks successfully completed & voice generated.", "SUCCESS")
            } else {
                repository.updateChunkStatus(chunk.id, ChunkStatus.FAILED.name, "TTS generation failed")
                repository.addLog(projectId, "TTS", "Chunk $chunkNum voice synthesis error.", "ERROR")
            }
        }

        // Check if all chunks are completed or skipped
        val updatedChunks = repository.getChunksOnce(projectId)
        val hasFailed = updatedChunks.any { it.status == ChunkStatus.FAILED.name }
        if (hasFailed) {
            val failedCount = updatedChunks.count { it.status == ChunkStatus.FAILED.name }
            repository.addLog(projectId, "Pipeline", "$failedCount chunk(s) encountered issues. You can retry failed chunks below.", "WARN")
            repository.updateProjectStatus(projectId, DubbingStatus.PAUSED.name)
            return
        }

        // Step 7: Audio Synchronization & Mixing
        repository.updateProgress(projectId, DubbingStatus.SYNCING_AUDIO.name, 82, totalChunks)
        repository.addLog(projectId, "AudioMixer", "Synchronizing Telugu speech timestamps and ducking original vocals...", "INFO")

        val allSegments = repository.getSegmentsOnce(projectId)
        val dubbedAudioPath = File(filesDir, "final_telugu_dubbed_audio.wav").absolutePath
        val origAudioPath = File(filesDir, "original_audio.wav").absolutePath

        val audioMode = try {
            AudioMode.valueOf(project.audioMode)
        } catch (e: Exception) {
            AudioMode.FULL_AI_MIX
        }

        audioProcessor.synchronizeAndMixAudio(
            projectId = projectId,
            segments = allSegments,
            originalAudioPath = origAudioPath,
            audioMode = audioMode,
            outputDubbedAudioPath = dubbedAudioPath
        )

        // Step 8: Final Video Rendering & Subtitles
        repository.updateProgress(projectId, DubbingStatus.RENDERING.name, 92, totalChunks)
        repository.addLog(projectId, "FFmpeg", "Muxing final dubbed video container with stereo audio track...", "INFO")

        val finalVideoPath = File(filesDir, "final_dubbed_movie.mp4").absolutePath
        val subResult = audioProcessor.generateSubtitles(allSegments, filesDir.absolutePath)
        val subs = subResult.getOrNull()

        audioProcessor.muxFinalDubbedVideo(
            originalVideoPath = project.videoUri ?: "movie.mp4",
            dubbedAudioPath = dubbedAudioPath,
            subtitlesPath = subs?.teluguSrtPath,
            outputVideoPath = finalVideoPath
        )

        repository.updateProgress(projectId, DubbingStatus.COMPLETED.name, 100, totalChunks)
        repository.saveProject(
            project.copy(
                status = DubbingStatus.COMPLETED.name,
                overallProgress = 100,
                currentChunkIndex = totalChunks,
                dubbedAudioPath = dubbedAudioPath,
                finalVideoPath = finalVideoPath,
                teluguSrtPath = subs?.teluguSrtPath,
                originalSrtPath = subs?.originalSrtPath,
                bilingualSrtPath = subs?.bilingualSrtPath,
                updatedAt = System.currentTimeMillis()
            )
        )
        repository.addLog(projectId, "Pipeline", "SUCCESS! Movie dubbing completed. Ready for preview and download.", "SUCCESS")
    }
}
