package org.vinaygopinath.launchchat.daos

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.vinaygopinath.launchchat.models.DetailedActivity

@Dao
interface DetailedActivityDao {

    @Query(
        """
            SELECT * FROM activities ORDER BY occurred_at DESC LIMIT 2
        """
    )
    fun getRecentDetailedActivities(): Flow<List<DetailedActivity>>
}