package org.vinaygopinath.launchchat.factories

import org.vinaygopinath.launchchat.extensions.withMillisecondPrecision
import org.vinaygopinath.launchchat.models.Action
import java.time.Instant

object ActionFactory {

    fun build(
        id: Long = 0,
        activityId: Long,
        phoneNumber: String = "+1333555777",
        type: Action.Type = Action.Type.SIGNAL,
        occurredAt: Instant = Instant.now().withMillisecondPrecision()
    ): Action {
        return Action(
            id = id,
            activityId = activityId,
            phoneNumber = phoneNumber,
            type = type,
            occurredAt = occurredAt
        )
    }
}