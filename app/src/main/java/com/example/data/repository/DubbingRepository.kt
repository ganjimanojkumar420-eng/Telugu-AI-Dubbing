package com.example.data.repository

import com.example.data.local.DubbingDao
import com.example.data.model.AudioChunkEntity
import com.example.data.model.ModelConfigEntity
import com.example.data.model.ProcessingLogEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.SpeakerProfileEntity
import com.example.data.model.TranscriptSegmentEntity
import kotlinx.coroutines.flow.Flow

class DubbingRepository(private val dao: DubbingDao) {

    val allProjects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    val modelConfig: Flow<ModelConfigEntity?> = dao.getModelConfig()

    fun getProject(projectId: String): Flow<ProjectEntity?> = dao.getProjectById(projectId)
    suspend fun getProjectOnce(projectId: String): ProjectEntity? = dao.getProjectByIdOnce(projectId)

    fun getChunks(projectId: String): Flow<List<AudioChunkEntity>> = dao.getChunksForProject(projectId)
    suspend fun getChunksOnce(projectId: String): List<AudioChunkEntity> = dao.getChunksForProjectOnce(projectId)

    fun getSegments(projectId: String): Flow<List<TranscriptSegmentEntity>> = dao.getSegmentsForProject(projectId)
    suspend fun getSegmentsOnce(projectId: String): List<TranscriptSegmentEntity> = dao.getSegmentsForProjectOnce(projectId)

    fun getSpeakers(projectId: String): Flow<List<SpeakerProfileEntity>> = dao.getSpeakersForProject(projectId)
    suspend fun getSpeakersOnce(projectId: String): List<SpeakerProfileEntity> = dao.getSpeakersForProjectOnce(projectId)

    fun getLogs(projectId: String): Flow<List<ProcessingLogEntity>> = dao.getLogsForProject(projectId)

    suspend fun saveProject(project: ProjectEntity) = dao.insertOrUpdateProject(project)
    suspend fun updateProjectStatus(projectId: String, status: String) = dao.updateProjectStatus(projectId, status)
    suspend fun updateProgress(projectId: String, status: String, progress: Int, currentChunk: Int) =
        dao.updateProjectProgress(projectId, status, progress, currentChunk)

    suspend fun deleteProject(projectId: String) {
        dao.deleteChunksForProject(projectId)
        dao.deleteSegmentsForProject(projectId)
        dao.deleteSpeakersForProject(projectId)
        dao.deleteLogsForProject(projectId)
        dao.deleteProject(projectId)
    }

    suspend fun insertChunks(chunks: List<AudioChunkEntity>) = dao.insertChunks(chunks)
    suspend fun updateChunk(chunk: AudioChunkEntity) = dao.updateChunk(chunk)
    suspend fun updateChunkStatus(chunkId: Long, status: String, error: String? = null) = dao.updateChunkStatus(chunkId, status, error)
    suspend fun getFailedChunks(projectId: String) = dao.getFailedChunks(projectId)

    suspend fun insertSegments(segments: List<TranscriptSegmentEntity>) = dao.insertSegments(segments)
    suspend fun updateSegment(segment: TranscriptSegmentEntity) = dao.updateSegment(segment)
    suspend fun updateTeluguTranslation(segmentId: Long, newTelugu: String) = dao.updateTeluguTranslation(segmentId, newTelugu)

    suspend fun insertSpeakers(speakers: List<SpeakerProfileEntity>) = dao.insertSpeakers(speakers)
    suspend fun updateSpeaker(speaker: SpeakerProfileEntity) = dao.updateSpeaker(speaker)

    suspend fun addLog(projectId: String, step: String, message: String, level: String = "INFO") {
        dao.insertLog(
            ProcessingLogEntity(
                projectId = projectId,
                step = step,
                message = message,
                level = level
            )
        )
    }

    suspend fun saveModelConfig(config: ModelConfigEntity) = dao.saveModelConfig(config)
    suspend fun getModelConfigOnce(): ModelConfigEntity = dao.getModelConfigOnce() ?: ModelConfigEntity()
}
