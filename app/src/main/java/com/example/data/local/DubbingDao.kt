package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AudioChunkEntity
import com.example.data.model.ModelConfigEntity
import com.example.data.model.ProcessingLogEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.SpeakerProfileEntity
import com.example.data.model.TranscriptSegmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DubbingDao {

    // Projects
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectById(projectId: String): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectByIdOnce(projectId: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("UPDATE projects SET status = :status, overallProgress = :progress, currentChunkIndex = :currentChunk, updatedAt = :timestamp WHERE id = :projectId")
    suspend fun updateProjectProgress(projectId: String, status: String, progress: Int, currentChunk: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE projects SET status = :status, updatedAt = :timestamp WHERE id = :projectId")
    suspend fun updateProjectStatus(projectId: String, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProject(projectId: String)

    // Audio Chunks
    @Query("SELECT * FROM audio_chunks WHERE projectId = :projectId ORDER BY chunkIndex ASC")
    fun getChunksForProject(projectId: String): Flow<List<AudioChunkEntity>>

    @Query("SELECT * FROM audio_chunks WHERE projectId = :projectId ORDER BY chunkIndex ASC")
    suspend fun getChunksForProjectOnce(projectId: String): List<AudioChunkEntity>

    @Query("SELECT * FROM audio_chunks WHERE projectId = :projectId AND status = 'FAILED'")
    suspend fun getFailedChunks(projectId: String): List<AudioChunkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<AudioChunkEntity>)

    @Update
    suspend fun updateChunk(chunk: AudioChunkEntity)

    @Query("UPDATE audio_chunks SET status = :status, errorMessage = :error WHERE id = :chunkId")
    suspend fun updateChunkStatus(chunkId: Long, status: String, error: String? = null)

    @Query("DELETE FROM audio_chunks WHERE projectId = :projectId")
    suspend fun deleteChunksForProject(projectId: String)

    // Transcript Segments
    @Query("SELECT * FROM transcript_segments WHERE projectId = :projectId ORDER BY startMs ASC")
    fun getSegmentsForProject(projectId: String): Flow<List<TranscriptSegmentEntity>>

    @Query("SELECT * FROM transcript_segments WHERE projectId = :projectId ORDER BY startMs ASC")
    suspend fun getSegmentsForProjectOnce(projectId: String): List<TranscriptSegmentEntity>

    @Query("SELECT * FROM transcript_segments WHERE id = :segmentId")
    suspend fun getSegmentById(segmentId: Long): TranscriptSegmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSegments(segments: List<TranscriptSegmentEntity>)

    @Update
    suspend fun updateSegment(segment: TranscriptSegmentEntity)

    @Query("UPDATE transcript_segments SET teluguText = :newTeluguText, isEdited = 1 WHERE id = :segmentId")
    suspend fun updateTeluguTranslation(segmentId: Long, newTeluguText: String)

    @Query("DELETE FROM transcript_segments WHERE projectId = :projectId")
    suspend fun deleteSegmentsForProject(projectId: String)

    // Speaker Profiles
    @Query("SELECT * FROM speaker_profiles WHERE projectId = :projectId ORDER BY id ASC")
    fun getSpeakersForProject(projectId: String): Flow<List<SpeakerProfileEntity>>

    @Query("SELECT * FROM speaker_profiles WHERE projectId = :projectId ORDER BY id ASC")
    suspend fun getSpeakersForProjectOnce(projectId: String): List<SpeakerProfileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeakers(speakers: List<SpeakerProfileEntity>)

    @Update
    suspend fun updateSpeaker(speaker: SpeakerProfileEntity)

    @Query("DELETE FROM speaker_profiles WHERE projectId = :projectId")
    suspend fun deleteSpeakersForProject(projectId: String)

    // Processing Logs
    @Query("SELECT * FROM processing_logs WHERE projectId = :projectId ORDER BY timestamp DESC LIMIT 200")
    fun getLogsForProject(projectId: String): Flow<List<ProcessingLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ProcessingLogEntity)

    @Query("DELETE FROM processing_logs WHERE projectId = :projectId")
    suspend fun deleteLogsForProject(projectId: String)

    // Model Config
    @Query("SELECT * FROM model_configs WHERE id = 1")
    fun getModelConfig(): Flow<ModelConfigEntity?>

    @Query("SELECT * FROM model_configs WHERE id = 1")
    suspend fun getModelConfigOnce(): ModelConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveModelConfig(config: ModelConfigEntity)
}
