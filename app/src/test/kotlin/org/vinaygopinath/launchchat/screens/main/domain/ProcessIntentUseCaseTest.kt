package org.vinaygopinath.launchchat.screens.main.domain

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.net.Uri
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProcessIntentUseCaseTest {

    private val useCase = ProcessIntentUseCase()

    @Test
    fun `returns "no content found" when intent is null`() {
        assertThat(useCase.execute(null))
            .isEqualTo(ProcessIntentUseCase.ExtractedContent.NoContentFound)
    }

    @Test
    fun `returns "no content found" when intent action is not expected`() {
        val intent = mock<Intent>().apply {
            whenever(action).thenReturn(Intent.ACTION_BOOT_COMPLETED)
        }
        assertThat(useCase.execute(intent))
            .isEqualTo(ProcessIntentUseCase.ExtractedContent.NoContentFound)
    }

    @Test
    fun `returns "no content found" when intent data is null (View action)`() {
        val intent = mock<Intent>().apply {
            whenever(action).thenReturn(Intent.ACTION_VIEW)
            whenever(dataString).thenReturn(null)
        }
        assertThat(useCase.execute(intent))
            .isEqualTo(ProcessIntentUseCase.ExtractedContent.NoContentFound)
    }

    @Test
    fun `returns phone number result from tel scheme URI (View action)`() {
        val phoneNumber = "+1987654321"
        val uri = "some-uri"
        val intent = mock<Intent>().apply {
            whenever(action).thenReturn(Intent.ACTION_VIEW)
            whenever(dataString).thenReturn("tel:$phoneNumber")
            whenever(toUri(0)).thenReturn(uri)
        }
        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.Result(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.TEL,
                phoneNumbers = listOf(phoneNumber),
                rawContent = uri
            )
        )
    }

    @Test
    fun `returns phone number result and message from sms scheme URI (View action)`() {
        val phoneNumber = "+1987654321"
        val message = "some-message"
        val uri = "some-uri"
        val intent = mock<Intent>().apply {
            whenever(action).thenReturn(Intent.ACTION_VIEW)
            whenever(dataString).thenReturn("sms:$phoneNumber?body=$message")
            whenever(toUri(0)).thenReturn(uri)
        }
        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.Result(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.SMS,
                phoneNumbers = listOf(phoneNumber),
                rawContent = uri,
                message = message
            )
        )
    }

    @Test
    fun `returns phone number result and message from smsto scheme URI (View action)`() {
        val phoneNumber = "+1987654321"
        val message = "some-message"
        val uri = "some-uri"
        val intent = mock<Intent>().apply {
            whenever(action).thenReturn(Intent.ACTION_VIEW)
            whenever(dataString).thenReturn("smsto:$phoneNumber?body=$message")
            whenever(toUri(0)).thenReturn(uri)
        }
        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.Result(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.SMS,
                phoneNumbers = listOf(phoneNumber),
                rawContent = uri,
                message = message
            )
        )
    }

    @Test
    fun `returns phone number result and message from mms scheme URI (View action)`() {
        val phoneNumber = "+1987654321"
        val message = "some-message"
        val uri = "some-uri"
        val intent = mock<Intent>().apply {
            whenever(action).thenReturn(Intent.ACTION_VIEW)
            whenever(dataString).thenReturn("mms:$phoneNumber?body=$message")
            whenever(toUri(0)).thenReturn(uri)
        }
        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.Result(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.MMS,
                phoneNumbers = listOf(phoneNumber),
                rawContent = uri,
                message = message
            )
        )
    }

    @Test
    fun `returns phone number result and message from mmsto scheme URI (View action)`() {
        val phoneNumber = "+1987654321"
        val message = "some-message"
        val uri = "some-uri"
        val intent = mock<Intent>().apply {
            whenever(action).thenReturn(Intent.ACTION_VIEW)
            whenever(dataString).thenReturn("mmsto:$phoneNumber?body=$message")
            whenever(toUri(0)).thenReturn(uri)
        }
        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.Result(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.MMS,
                phoneNumbers = listOf(phoneNumber),
                rawContent = uri,
                message = message
            )
        )
    }

    @Test
    fun `returns possible result when data is of unknown scheme (View action)`() {
        val dataString = "some-data-string-with-possible-phone-number-88362522-and-other-text"
        val uri = "some-uri"
        val intent = spy<Intent>().apply {
            action = Intent.ACTION_VIEW
            whenever(this.dataString).thenReturn(dataString)
            whenever(toUri(0)).thenReturn(uri)
        }

        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.PossibleResult(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.UNKNOWN,
                rawInputText = dataString,
                rawContent = uri
            )
        )
    }

    @Test
    fun `returns "no content found" when intent data is null (Send action)`() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            clipData = buildClipDataWithContent(null)
        }
        assertThat(useCase.execute(intent))
            .isEqualTo(ProcessIntentUseCase.ExtractedContent.NoContentFound)
    }

    @Test
    fun `returns phone number result from tel scheme URI (Send action)`() {
        val phoneNumber = "+1987654321"
        val uri = "some-uri"
        val intent = spy(Intent()).apply {
            action = Intent.ACTION_SEND
            clipData = buildClipDataWithContent("tel:$phoneNumber")
            whenever(toUri(0)).thenReturn(uri)
        }
        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.Result(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.TEXT_SHARE,
                phoneNumbers = listOf(phoneNumber),
                rawContent = uri
            )
        )
    }

    @Test
    fun `returns phone number result and message from sms scheme URI (Send action)`() {
        val phoneNumber = "+1987654321"
        val message = "some-message"
        val uri = "some-uri"
        val intent = spy(Intent()).apply {
            action = Intent.ACTION_SEND
            clipData = buildClipDataWithContent("sms:$phoneNumber?body=$message")
            whenever(toUri(0)).thenReturn(uri)
        }

        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.Result(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.TEXT_SHARE,
                phoneNumbers = listOf(phoneNumber),
                rawContent = uri,
                message = message
            )
        )
    }

    @Test
    fun `returns phone number result and message from smsto scheme URI (Send action)`() {
        val phoneNumber = "+1987654321"
        val message = "some-message"
        val uri = "some-uri"
        val intent = spy(Intent()).apply {
            action = Intent.ACTION_SEND
            clipData = buildClipDataWithContent("smsto:$phoneNumber?body=$message")
            whenever(toUri(0)).thenReturn(uri)
        }

        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.Result(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.TEXT_SHARE,
                phoneNumbers = listOf(phoneNumber),
                rawContent = uri,
                message = message
            )
        )
    }

    @Test
    fun `returns phone number result and message from mms scheme URI (Send action)`() {
        val phoneNumber = "+1987654321"
        val message = "some-message"
        val uri = "some-uri"
        val intent = spy(Intent()).apply {
            action = Intent.ACTION_SEND
            clipData = buildClipDataWithContent("mms:$phoneNumber?body=$message")
            whenever(toUri(0)).thenReturn(uri)
        }

        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.Result(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.TEXT_SHARE,
                phoneNumbers = listOf(phoneNumber),
                rawContent = uri,
                message = message
            )
        )
    }

    @Test
    fun `returns phone number result and message from mmsto scheme URI (Send action)`() {
        val phoneNumber = "+1987654321"
        val message = "some-message"
        val uri = "some-uri"
        val intent = spy(Intent()).apply {
            action = Intent.ACTION_SEND
            clipData = buildClipDataWithContent("mmsto:$phoneNumber?body=$message")
            whenever(toUri(0)).thenReturn(uri)
        }

        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.Result(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.TEXT_SHARE,
                phoneNumbers = listOf(phoneNumber),
                rawContent = uri,
                message = message
            )
        )
    }

    @Test
    fun `returns possible result when data is of unknown scheme (Send action)`() {
        val clipboardString = "some-string-with-possible-phone-number-88362522-and-other-text"
        val uri = "some-uri"
        val intent = spy<Intent>().apply {
            action = Intent.ACTION_SEND
            clipData = buildClipDataWithContent(clipboardString)
            whenever(toUri(0)).thenReturn(uri)
        }

        assertThat(useCase.execute(intent)).isEqualTo(
            ProcessIntentUseCase.ExtractedContent.PossibleResult(
                source = ProcessIntentUseCase.ExtractedContent.ContentSource.TEXT_SHARE,
                rawInputText = clipboardString,
                rawContent = uri
            )
        )
    }

    private fun buildClipDataWithContent(text: String?): ClipData {
        return ClipData.newPlainText("Some label", text)
    }
}