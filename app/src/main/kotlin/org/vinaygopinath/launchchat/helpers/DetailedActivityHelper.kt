package org.vinaygopinath.launchchat.helpers

import android.content.res.Resources
import androidx.annotation.StringRes
import org.vinaygopinath.launchchat.R
import org.vinaygopinath.launchchat.models.Action
import org.vinaygopinath.launchchat.models.Activity
import org.vinaygopinath.launchchat.models.DetailedActivity
import org.vinaygopinath.launchchat.utils.DateUtils
import javax.inject.Inject

class DetailedActivityHelper @Inject constructor(
    private val dateUtils: DateUtils,
    private val resources: Resources
) {

    @StringRes
    fun getSourceDisplayName(detailedActivity: DetailedActivity): Int {
        return when (detailedActivity.activity.source) {
            Activity.Source.TEL -> R.string.activity_source_tel
            Activity.Source.SMS -> R.string.activity_source_sms
            Activity.Source.MMS -> R.string.activity_source_mms
            Activity.Source.TEXT_SHARE -> R.string.activity_source_text_share
            Activity.Source.CONTACT_FILE -> R.string.activity_source_contact
            Activity.Source.DIAL -> R.string.activity_source_dial
            Activity.Source.UNKNOWN -> R.string.activity_source_unknown
            Activity.Source.MANUAL_INPUT -> R.string.activity_source_manual_input
            Activity.Source.HISTORY -> R.string.activity_source_history
        }
    }

    fun getActivityShortTimestamp(detailedActivity: DetailedActivity): String {
        return dateUtils.getDateTimeString(detailedActivity.activity.occurredAt)
    }

    fun getActivityContent(detailedActivity: DetailedActivity): String {
        return detailedActivity.activity.content
    }

    fun isFirstActionVisible(detailedActivity: DetailedActivity): Boolean {
        return detailedActivity.actions.isNotEmpty()
    }

    fun getFirstActionText(detailedActivity: DetailedActivity): String? {
        return if (isFirstActionVisible(detailedActivity)) {
            getActionText(detailedActivity.actions.first())
        } else {
            null
        }
    }

    fun isSecondActionVisible(detailedActivity: DetailedActivity): Boolean {
        return detailedActivity.actions.size >= 2
    }

    fun getSecondActionText(detailedActivity: DetailedActivity): String? {
        return if (isSecondActionVisible(detailedActivity)) {
            getActionText(detailedActivity.actions[1])
        } else {
            null
        }
    }

    private fun getActionText(action: Action): String {
        return resources.getString(
            R.string.action_label,
            action.phoneNumber,
            getActionTypeDisplayName(action)
        )
    }

    private fun getActionTypeDisplayName(action: Action): String {
        return resources.getString(
            when (action.type) {
                Action.Type.WHATSAPP -> R.string.action_type_whatsapp
                Action.Type.SIGNAL -> R.string.action_type_signal
                Action.Type.TELEGRAM -> R.string.action_type_telegram
            }
        )
    }

    fun isMoreTextVisible(detailedActivity: DetailedActivity): Boolean {
        return detailedActivity.actions.size > 2
    }

    fun getMoreText(detailedActivity: DetailedActivity): String? {
        return if (isMoreTextVisible(detailedActivity)) {
            resources.getString(R.string.action_more, detailedActivity.actions.size - 2)
        } else {
            null
        }
    }
}