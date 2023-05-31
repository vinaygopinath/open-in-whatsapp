package org.vinaygopinath.launchchat.helpers

import android.content.Intent
import android.content.Intent.*
import androidx.core.net.toUri
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IntentHelperProcessLaunchIntentTest {

    private val helper = IntentHelper()

    @Test
    fun `returns empty when intent is empty`() {
        val processedIntent = helper.processLaunchIntent(null)

        assertThat(processedIntent).isEqualTo(IntentHelper.ProcessedIntent.Empty)
    }

    @Test
    fun `returns empty when intent has ACTION_MAIN`() {
        val mainIntent = Intent().apply {
            action = ACTION_MAIN
            data = null
        }

        val processedIntent = helper.processLaunchIntent(mainIntent)

        assertThat(processedIntent).isEqualTo(IntentHelper.ProcessedIntent.Empty)
    }

    @Test
    fun `returns phone number when intent has ACTION_VIEW and data contains a tel URI`() {
        val somePhoneNumber = "123456789"
        val viewIntent = Intent().apply {
            action = ACTION_VIEW
            data = "tel:$somePhoneNumber".toUri()
        }

        val processedIntent = helper.processLaunchIntent(viewIntent)

        assertThat(processedIntent).isInstanceOf(IntentHelper.ProcessedIntent.TelUriScheme::class.java)
        assertThat((processedIntent as IntentHelper.ProcessedIntent.TelUriScheme).phoneNumber)
            .isEqualTo(somePhoneNumber)
    }

    @Test
    fun `returns empty when intent is not ACTION_VIEW or data does not contain a tel URI`() {
        val someIntent = Intent().apply {
            action = ACTION_ANSWER
            data = "https://some-website.com".toUri()
        }

        val processedIntent = helper.processLaunchIntent(someIntent)

        assertThat(processedIntent).isEqualTo(IntentHelper.ProcessedIntent.Empty)
    }
}