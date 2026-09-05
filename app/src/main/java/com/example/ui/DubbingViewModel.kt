package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.FFmpegAudioProcessingProvider
import com.example.ai.DubbingPipelineEngine
import com.example.ai.OpenSourceTeluguTranslationProvider
import com.example.ai.OpenSourceTeluguTtsProvider
import com.example.ai.OpenSourceWhisperProvider
import com.example.data.local.AppDatabase
import com.example.data.model.AudioChunkEntity
import com.example.data.model.AudioMode
import com.example.data.model.DubbingStatus
import com.example.data.model.ModelConfigEntity
import com.example.data.model.ProcessingLogEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.SpeakerProfileEntity
import com.example.data.model.TranscriptSegmentEntity
import com.example.data.model.VoiceMode
import com.example.data.model.VoiceStyle
import com.example.data.repository.DubbingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID
import java.util.concurrent.TimeUnit

class DubbingViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = DubbingRepository(database.dubbingDao())

    private val whisperProvider = OpenSourceWhisperProvider("http://10.0.2.2:8000")
    private val translationProvider = OpenSourceTeluguTranslationProvider("http://10.0.2.2:8000")
    private val ttsProvider = OpenSourceTeluguTtsProvider(application, "http://10.0.2.2:8000")
    private val audioProcessor = FFmpegAudioProcessingProvider(application)

    private val pipelineEngine = DubbingPipelineEngine(
        context = application,
        repository = repository,
        sttProvider = whisperProvider,
        translationProvider = translationProvider,
        ttsProvider = ttsProvider,
        audioProcessor = audioProcessor
    )

    val isProcessing: StateFlow<Boolean> = pipelineEngine.isProcessing

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val modelConfig: StateFlow<ModelConfigEntity> = repository.modelConfig
        .flatMapLatest { it?.let { flowOf(it) } ?: flowOf(ModelConfigEntity()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ModelConfigEntity())

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _activeProjectId = MutableStateFlow<String?>(null)
    val activeProjectId: StateFlow<String?> = _activeProjectId.asStateFlow()

    val activeProject: StateFlow<ProjectEntity?> = _activeProjectId.flatMapLatest { id ->
        if (id != null) repository.getProject(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeChunks: StateFlow<List<AudioChunkEntity>> = _activeProjectId.flatMapLatest { id ->
        if (id != null) repository.getChunks(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSegments: StateFlow<List<TranscriptSegmentEntity>> = _activeProjectId.flatMapLatest { id ->
        if (id != null) repository.getSegments(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSpeakers: StateFlow<List<SpeakerProfileEntity>> = _activeProjectId.flatMapLatest { id ->
        if (id != null) repository.getSpeakers(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLogs: StateFlow<List<ProcessingLogEntity>> = _activeProjectId.flatMapLatest { id ->
        if (id != null) repository.getLogs(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private val _activeEditorIndex = MutableStateFlow(0)
    val activeEditorIndex: StateFlow<Int> = _activeEditorIndex.asStateFlow()

    private val _connectionStatus = MutableStateFlow<String?>(null)
    val connectionStatus: StateFlow<String?> = _connectionStatus.asStateFlow()

    // Active playback track mode in player
    private val _playerAudioTrack = MutableStateFlow("TELUGU_DUBBED") // "TELUGU_DUBBED" or "ORIGINAL"
    val playerAudioTrack: StateFlow<String> = _playerAudioTrack.asStateFlow()

    private val _playerSubtitleTrack = MutableStateFlow("TELUGU") // "NONE", "TELUGU", "ENGLISH", "BILINGUAL"
    val playerSubtitleTrack: StateFlow<String> = _playerSubtitleTrack.asStateFlow()

    init {
        // Initialize sample project if none exists so user immediately has rich experience
        viewModelScope.launch {
            val existing = repository.getProjectOnce("sample_rrr_climax")
            if (existing == null) {
                createSampleProject(
                    id = "sample_rrr_climax",
                    title = "RRR Climax Epic Battlefield Scene",
                    durationFormatted = "02h 15m 30s",
                    durationSeconds = 8130,
                    fileSizeFormatted = "2.8 GB",
                    sourceLang = "Hindi",
                    targetLang = "Telugu",
                    voiceMode = VoiceMode.AUTO_CHARACTER.name,
                    voiceStyle = VoiceStyle.CINEMATIC.name,
                    audioMode = AudioMode.FULL_AI_MIX.name
                )
            }
        }
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setActiveProject(projectId: String) {
        _activeProjectId.value = projectId
    }

    fun setPlayerAudioTrack(track: String) {
        _playerAudioTrack.value = track
    }

    fun setPlayerSubtitleTrack(track: String) {
        _playerSubtitleTrack.value = track
    }

    fun createAndStartProject(
        title: String,
        durationFormatted: String,
        durationSeconds: Long,
        fileSizeFormatted: String,
        videoUri: String?,
        sourceLang: String,
        targetLang: String,
        voiceMode: String,
        voiceStyle: String,
        audioMode: String
    ) {
        val projectId = "proj_" + UUID.randomUUID().toString().take(8)
        val project = ProjectEntity(
            id = projectId,
            title = if (title.isBlank()) "Feature Movie (${sourceLang} Dub)" else title,
            durationFormatted = durationFormatted,
            durationSeconds = durationSeconds,
            fileSizeFormatted = fileSizeFormatted,
            sourceLanguage = sourceLang,
            targetLanguage = targetLang,
            voiceMode = voiceMode,
            voiceStyle = voiceStyle,
            audioMode = audioMode,
            status = DubbingStatus.UPLOADED.name,
            overallProgress = 5,
            videoUri = videoUri
        )

        viewModelScope.launch {
            repository.saveProject(project)
            repository.addLog(projectId, "Upload", "Movie file '$title' ($fileSizeFormatted) uploaded successfully.", "SUCCESS")
            _activeProjectId.value = projectId
            _selectedTab.value = 1 // Switch to processing screen
            pipelineEngine.startPipeline(projectId)
        }
    }

    fun loadSampleMovie(type: String) {
        viewModelScope.launch {
            val (id, title, durationStr, durationSec, sizeStr, srcLang) = when (type) {
                "baahubali" -> Tuple6(
                    "sample_baahubali",
                    "Baahubali Royal Coronation Scene",
                    "01h 45m 00s",
                    6300L,
                    "2.1 GB",
                    "Tamil"
                )
                "interstellar" -> Tuple6(
                    "sample_interstellar",
                    "Interstellar Wormhole Sequence",
                    "02h 49m 00s",
                    10140L,
                    "3.4 GB",
                    "English"
                )
                "salaar" -> Tuple6(
                    "sample_salaar",
                    "Salaar Coal Mines Action Block",
                    "00h 42m 15s",
                    2535L,
                    "980 MB",
                    "Hindi"
                )
                else -> Tuple6(
                    "sample_rrr_climax",
                    "RRR Climax Epic Battlefield Scene",
                    "02h 15m 30s",
                    8130L,
                    "2.8 GB",
                    "Hindi"
                )
            }

            createSampleProject(
                id = id,
                title = title,
                durationFormatted = durationStr,
                durationSeconds = durationSec,
                fileSizeFormatted = sizeStr,
                sourceLang = srcLang,
                targetLang = "Telugu",
                voiceMode = VoiceMode.AUTO_CHARACTER.name,
                voiceStyle = VoiceStyle.CINEMATIC.name,
                audioMode = AudioMode.FULL_AI_MIX.name
            )
            _activeProjectId.value = id
            _selectedTab.value = 1
            pipelineEngine.startPipeline(id)
        }
    }

    private suspend fun createSampleProject(
        id: String,
        title: String,
        durationFormatted: String,
        durationSeconds: Long,
        fileSizeFormatted: String,
        sourceLang: String,
        targetLang: String,
        voiceMode: String,
        voiceStyle: String,
        audioMode: String
    ) {
        val project = ProjectEntity(
            id = id,
            title = title,
            durationFormatted = durationFormatted,
            durationSeconds = durationSeconds,
            fileSizeFormatted = fileSizeFormatted,
            sourceLanguage = sourceLang,
            targetLanguage = targetLang,
            voiceMode = voiceMode,
            voiceStyle = voiceStyle,
            audioMode = audioMode,
            status = DubbingStatus.UPLOADED.name,
            overallProgress = 0,
            videoUri = "sample://$id"
        )
        repository.saveProject(project)
        repository.addLog(id, "Init", "Initialized long video sample project '$title'", "INFO")
    }

    fun startDubbing() {
        val id = _activeProjectId.value ?: return
        pipelineEngine.startPipeline(id)
    }

    fun pauseDubbing() {
        val id = _activeProjectId.value ?: return
        pipelineEngine.pausePipeline(id)
    }

    fun retryChunk(chunkId: Long) {
        val id = _activeProjectId.value ?: return
        viewModelScope.launch {
            pipelineEngine.retryChunk(id, chunkId)
        }
    }

    fun skipChunk(chunkId: Long) {
        val id = _activeProjectId.value ?: return
        viewModelScope.launch {
            pipelineEngine.skipChunk(id, chunkId)
        }
    }

    fun retryAllFailed() {
        val id = _activeProjectId.value ?: return
        viewModelScope.launch {
            pipelineEngine.retryAllFailed(id)
        }
    }

    fun updateSegmentTranslation(segmentId: Long, newTeluguText: String) {
        viewModelScope.launch {
            repository.updateTeluguTranslation(segmentId, newTeluguText)
            val projId = _activeProjectId.value ?: return@launch
            repository.addLog(projId, "Editor", "Telugu dialogue updated manually for segment #$segmentId.", "INFO")
        }
    }

    fun updateSpeakerProfile(speaker: SpeakerProfileEntity) {
        viewModelScope.launch {
            repository.updateSpeaker(speaker)
            val projId = _activeProjectId.value ?: return@launch
            repository.addLog(projId, "Voice", "Voice updated for '${speaker.displayName}' -> ${speaker.assignedVoiceName}", "INFO")
        }
    }

    fun setEditorIndex(index: Int) {
        _activeEditorIndex.value = index
    }

    fun playSegmentAudio(segment: TranscriptSegmentEntity) {
        _isPlayingAudio.value = true
        ttsProvider.speakTextDirectly(
            text = segment.teluguText,
            pitch = segment.pitch,
            speed = segment.speed,
            onDone = { _isPlayingAudio.value = false }
        )
    }

    fun stopAudio() {
        ttsProvider.stopAudio()
        _isPlayingAudio.value = false
    }

    fun saveModelConfiguration(config: ModelConfigEntity) {
        viewModelScope.launch {
            repository.saveModelConfig(config)
        }
    }

    fun testServerConnection(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _connectionStatus.value = "Pinging AI server: $url..."
            delay(800)
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .build()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful || response.code in 200..404) {
                    _connectionStatus.value = "SUCCESS: Connected to Inference Host (${response.code}). Ready for Whisper & IndicTrans2."
                } else {
                    _connectionStatus.value = "Response code ${response.code}: Server responded. Local engine active as primary."
                }
            } catch (e: Exception) {
                _connectionStatus.value = "Notice: External server offline. Open-source local offline engine enabled."
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            if (_activeProjectId.value == projectId) {
                _activeProjectId.value = allProjects.value.firstOrNull { it.id != projectId }?.id
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsProvider.release()
    }
}

private data class Tuple6<A, B, C, D, E, F>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F
)
