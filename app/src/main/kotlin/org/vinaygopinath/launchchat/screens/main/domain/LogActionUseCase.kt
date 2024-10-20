package org.vinaygopinath.launchchat.screens.main.domain

import org.vinaygopinath.launchchat.models.Action
import org.vinaygopinath.launchchat.models.Activity
import org.vinaygopinath.launchchat.models.Activity.Source
import org.vinaygopinath.launchchat.repositories.ActionRepository
import org.vinaygopinath.launchchat.repositories.ActivityRepository
import org.vinaygopinath.launchchat.utils.DateUtils
import java.time.Instant
import javax.inject.Inject

class LogActionUseCase @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val actionRepository: ActionRepository,
    private val dateUtils: DateUtils
) {

    suspend fun execute(
        type: Action.Type,
        number: String,
        message: String?,
        activity: Activity?,
        rawInputText: String
    ) {
        val currentTime = dateUtils.getCurrentInstant()
        val associatedActivity = activity ?: createActivity(message, rawInputText, currentTime)

        actionRepository.create(
            Action(
                activityId = associatedActivity.id,
                phoneNumber = number,
                type = type,
                occurredAt = currentTime
            )
        )
    }

    private suspend fun createActivity(
        message: String?,
        rawInputText: String,
        currentTime: Instant
    ): Activity {
        val activity = Activity(
            content = rawInputText,
            source = Source.MANUAL_INPUT,
            message = message,
            occurredAt = currentTime
        )

        return activityRepository.create(activity)
    }
}