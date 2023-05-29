package org.vinaygopinath.openinchat.helpers

import android.content.Intent
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.core.net.toUri
import org.vinaygopinath.openinchat.Constants
import javax.inject.Inject

class IntentHelper @Inject constructor() {

    fun getGithubRepoIntent(): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(Constants.GITHUB_REPO_URL)
        }
    }

    fun getOpenWhatsappIntent(phoneNumber: String, message: String?): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = generateWhatsappUrl(phoneNumber, message).toUri()
        }
    }

    fun getOpenSignalIntent(phoneNumber: String): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = generateSignalUrl(phoneNumber).toUri()
        }
    }

    @VisibleForTesting
    fun generateWhatsappUrl(phoneNumber: String, message: String?): String {
        val builder = StringBuilder()
        builder.append("https://wa.me/$phoneNumber/")
        if (message != null) {
            builder.append("?text=$message")
        }

        return builder.toString()
    }

    @VisibleForTesting
    fun generateSignalUrl(phoneNumber: String): String {
        return "https://signal.me/#p/${phoneNumber}"
    }
}