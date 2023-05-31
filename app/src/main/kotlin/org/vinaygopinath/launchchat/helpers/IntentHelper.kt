package org.vinaygopinath.launchchat.helpers

import android.content.Intent
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.core.net.toUri
import org.vinaygopinath.launchchat.Constants
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

    fun getOpenTelegramIntent(phoneNumber: String): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            data = generateTelegramUrl(phoneNumber).toUri()
        }
    }

    fun processLaunchIntent(intent: Intent?): ProcessedIntent {
        if (intent != null && interestedActions.contains(intent.action)) {
            val action = intent.action
            val data = intent.data
            var processedIntent: ProcessedIntent = ProcessedIntent.Empty
            if (action == Intent.ACTION_VIEW && data != null && data.toString().trim().startsWith("tel:")) {
                processedIntent = ProcessedIntent.TelUriScheme(intent.data.toString().substring(4).trim())
            } else if (action == Intent.ACTION_SEND && intent.clipData != null) {
                val clipDataStr = intent.clipData?.getItemAt(0)?.text.toString().trim()
                if (clipDataStr.startsWith("tel:")) {
                    processedIntent = ProcessedIntent.TelUriScheme(clipDataStr.substring(4))
                }
            }

            return processedIntent
        }

        return ProcessedIntent.Empty
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

    @VisibleForTesting
    fun generateTelegramUrl(phoneNumber: String): String {
        return "https://t.me/${phoneNumber}"
    }

    private val interestedActions = arrayOf(
        Intent.ACTION_VIEW,
        Intent.ACTION_SEND
    )

    sealed class ProcessedIntent {
        data class TelUriScheme(val phoneNumber: String) : ProcessedIntent()
        object Empty : ProcessedIntent()
    }
}