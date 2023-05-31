package org.vinaygopinath.launchchat.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.vinaygopinath.launchchat.R

class Prefs(private val context: Context) {

    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /* Automagic prefs: these are not real prefs. They are just set by the app to remember stuff */
    val LAST_REGION: String = context.getString(R.string.pref_last_region)

    var lastRegion: String
        get() = prefs.getString(LAST_REGION, "") ?: ""
        set(value) = setPreference(LAST_REGION, value)

    private fun setPreference(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}
