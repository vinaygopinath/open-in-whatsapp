package org.vinaygopinath.launchchat.screens.main.domain

import android.content.Intent
import android.net.Uri
import javax.inject.Inject

class ProcessIntentUseCase @Inject constructor() {

    fun execute(intent: Intent?): ExtractedContent {
        return when {
            intent == null || !INTERESTED_ACTIONS.contains(intent.action) -> ExtractedContent.NoContentFound
            intent.action == Intent.ACTION_VIEW -> processViewIntent(intent)
            intent.action == Intent.ACTION_SEND -> processClipboardIntent(intent)
            intent.action == Intent.ACTION_DIAL -> processDialIntent(intent)
            else -> ExtractedContent.PossibleResult(
                source = ExtractedContent.ContentSource.UNKNOWN,
                rawInputText = intent.dataString?.trim(),
                rawContent = intent.toUri(0)
            )
        }
    }

    private fun processViewIntent(intent: Intent): ExtractedContent {
        val data = intent.dataString?.trim()
        return when {
            data == null -> ExtractedContent.NoContentFound
            doesTextStartWithTelScheme(data) -> {
                extractTelSchemeResult(data, intent)
            }

            doesTextStartWithMessageScheme(data) -> {
                extractMessageSchemeResult(data, intent)
            }

            else -> {
                ExtractedContent.PossibleResult(
                    source = ExtractedContent.ContentSource.UNKNOWN,
                    rawInputText = intent.dataString?.trim(),
                    rawContent = intent.toUri(0)
                )
            }
        }
    }

    private fun processClipboardIntent(intent: Intent): ExtractedContent {
        val clipData = intent.clipData?.getItemAt(0)?.text?.toString()?.trim()
        return when {
            clipData == null -> ExtractedContent.NoContentFound
            doesTextStartWithTelScheme(clipData) -> extractTelSchemeResult(
                clipData,
                intent,
                ExtractedContent.ContentSource.TEXT_SHARE
            )

            doesTextStartWithMessageScheme(clipData) -> extractMessageSchemeResult(
                clipData,
                intent,
                ExtractedContent.ContentSource.TEXT_SHARE
            )

            else -> ExtractedContent.PossibleResult(
                source = ExtractedContent.ContentSource.TEXT_SHARE,
                rawInputText = clipData.trim(),
                rawContent = intent.toUri(0)
            )
        }
    }

    private fun processDialIntent(intent: Intent): ExtractedContent {
        val data = intent.dataString?.trim()
        return when {
            data == null -> ExtractedContent.NoContentFound
            doesTextStartWithTelScheme(data) -> {
                extractTelSchemeResult(data, intent, ExtractedContent.ContentSource.DIAL)
            }

            else -> {
                ExtractedContent.PossibleResult(
                    source = ExtractedContent.ContentSource.UNKNOWN,
                    rawInputText = intent.dataString?.trim(),
                    rawContent = intent.toUri(0)
                )
            }
        }
    }

    private fun doesTextStartWithTelScheme(text: String): Boolean {
        return text.startsWith(TEL_SCHEME, ignoreCase = true)
    }

    private fun extractTelSchemeResult(
        text: String,
        intent: Intent,
        source: ExtractedContent.ContentSource = ExtractedContent.ContentSource.TEL
    ): ExtractedContent {
        return ExtractedContent.Result(
            source = source,
            phoneNumbers = listOf(text.substring(TEL_SCHEME.length).trim()),
            rawContent = intent.toUri(0)
        )
    }

    private fun doesTextStartWithMessageScheme(text: String): Boolean {
        return MESSAGE_SCHEMES.any { text.startsWith(it, ignoreCase = true) }
    }

    private fun extractMessageSchemeResult(
        text: String,
        intent: Intent,
        source: ExtractedContent.ContentSource? = null
    ): ExtractedContent.Result {
        println("LLCH: Extracting message result")
        val messageScheme = MESSAGE_SCHEMES.first { text.startsWith(it, ignoreCase = true) }
        val dataWithoutScheme = text.substring(messageScheme.length)

        return ExtractedContent.Result(
            source = source ?: getContentSourceFromMessageScheme(messageScheme),
            phoneNumbers = getPhoneNumbersFromMessageLink(dataWithoutScheme),
            message = getMessageFromMessageLink(dataWithoutScheme),
            rawContent = intent.toUri(0)
        )
    }


    private fun getContentSourceFromMessageScheme(messageScheme: String): ExtractedContent.ContentSource {
        return when (messageScheme) {
            "sms:", "smsto:" -> ExtractedContent.ContentSource.SMS
            "mms:", "mmsto:" -> ExtractedContent.ContentSource.MMS
            else -> throw IllegalStateException("getContentSourceFromMessageScheme was called with an unknown scheme: \"$messageScheme\"")
        }
    }

    private fun getPhoneNumbersFromMessageLink(dataWithoutScheme: String): List<String> {
        // See Kotlin playground for testing: https://pl.kotl.in/4CyPvWiDJ
        val phoneNumberFullString = dataWithoutScheme.split("?")
        return phoneNumberFullString.first().split(",").map { it.trim() }
    }

    private fun getMessageFromMessageLink(dataWithoutScheme: String): String {
        return if (dataWithoutScheme.contains("?body=")) {
            Uri.decode(dataWithoutScheme.split("?body=")[1])
        } else {
            ""
        }.trim()
    }

    sealed class ExtractedContent {
        data class PossibleResult(
            val source: ContentSource,
            val rawInputText: String?,
            val rawContent: String
        ) : ExtractedContent()

        data class Result(
            val source: ContentSource,
            val phoneNumbers: List<String>,
            val rawContent: String,
            val message: String? = null
        ) : ExtractedContent()

        data object NoContentFound : ExtractedContent()

        enum class ContentSource {
            TEL, SMS, MMS, TEXT_SHARE, CONTACT_FILE, DIAL, UNKNOWN
        }
    }

    companion object {
        private val INTERESTED_ACTIONS = listOf(
            Intent.ACTION_VIEW,
            Intent.ACTION_DIAL,
            Intent.ACTION_SEND,
            Intent.ACTION_SENDTO,
            Intent.ACTION_SEND_MULTIPLE
        )

        const val TEL_SCHEME = "tel:"
        val MESSAGE_SCHEMES = listOf("smsto:", "sms:", "mmsto:", "mms:")
    }
}