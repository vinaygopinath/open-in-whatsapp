package org.vinaygopinath.launchchat.factories

import org.vinaygopinath.launchchat.models.Settings

object SettingsFactory {

    fun build(
        isActivityHistoryEnabled: Boolean = true
    ): Settings = Settings(
        isActivityHistoryEnabled = isActivityHistoryEnabled
    )
}