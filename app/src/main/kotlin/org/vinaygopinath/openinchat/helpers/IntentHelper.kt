package org.vinaygopinath.openinchat.helpers

import android.content.Intent
import android.net.Uri
import org.vinaygopinath.openinchat.Constants
import javax.inject.Inject

class IntentHelper @Inject constructor() {

    fun getGithubRepoIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(Constants.GITHUB_REPO_URL)
        }
    }
}