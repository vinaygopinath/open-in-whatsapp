package org.vinaygopinath.launchchat.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "activities",
    indices = [
        Index("occurred_at")
    ]
)
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo val content: String,
    @ColumnInfo val source: Source,
    @ColumnInfo val message: String?,
    @ColumnInfo("occurred_at") val occurredAt: Instant
) {
    enum class Source {
        TEL,
        SMS,
        MMS,
        TEXT_SHARE,
        CONTACT_FILE,
        DIAL,
        UNKNOWN,
        MANUAL_INPUT,
        HISTORY
    }
}

