package org.vinaygopinath.launchchat.repositories

import kotlinx.coroutines.flow.Flow
import org.vinaygopinath.launchchat.daos.DetailedActivityDao
import org.vinaygopinath.launchchat.models.DetailedActivity
import javax.inject.Inject

class DetailedActivityRepository @Inject constructor(
    private val detailedActivityDao: DetailedActivityDao
) {

    fun getRecentDetailedActivities(): Flow<List<DetailedActivity>> {
        return detailedActivityDao.getRecentDetailedActivities()
    }
}