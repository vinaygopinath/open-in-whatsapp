package org.vinaygopinath.launchchat.screens.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.vinaygopinath.launchchat.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}