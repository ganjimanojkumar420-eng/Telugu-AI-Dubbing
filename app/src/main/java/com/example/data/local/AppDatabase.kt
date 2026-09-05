package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AudioChunkEntity
import com.example.data.model.ModelConfigEntity
import com.example.data.model.ProcessingLogEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.SpeakerProfileEntity
import com.example.data.model.TranscriptSegmentEntity

@Database(
    entities = [
        ProjectEntity::class,
        AudioChunkEntity::class,
        TranscriptSegmentEntity::class,
        SpeakerProfileEntity::class,
        ProcessingLogEntity::class,
        ModelConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dubbingDao(): DubbingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "manoj_movie_dubbing.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
