package org.vinaygopinath.openinchat.helpers

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock

@RunWith(JUnit4::class)
class PhoneNumberHelperExtractNumberTest {

    private val helper = PhoneNumberHelper(mock())

    @Test
    fun `retains the country code when the raw string contains a country code`() {
        val extractedNumbers = helper.extractPhoneNumber("+1555555555")

        assertThat(extractedNumbers).containsExactly("+1555555555")
    }

    @Test
    fun `extracts the phone number when the raw string contains parentheses`() {
        val extractedNumbers = helper.extractPhoneNumber("+1(555)555555")

        assertThat(extractedNumbers).containsExactly("+1555555555")
    }

    @Test
    fun `extracts the phone number when the raw string contains hyphens`() {
        val extractedNumbers = helper.extractPhoneNumber("+1 123 456-7890")

        assertThat(extractedNumbers).containsExactly("+11234567890")
    }

    @Test
    fun `extracts the phone number when the raw string contains spaces`() {
        val extractedNumbers = helper.extractPhoneNumber("+1 123 456 7890")

        assertThat(extractedNumbers).containsExactly("+11234567890")
    }

    @Test
    fun `extracts the phone number when the raw string contains a country code, parentheses, hyphens and spaces`() {
        val extractedNumbers = helper.extractPhoneNumber("+1 (123) - 456-7890")

        assertThat(extractedNumbers).containsExactly("+11234567890")
    }

    @Test
    fun `extracts the phone number when the raw string contains a phone number in tel URI scheme`() {
        val extractedNumbers = helper.extractPhoneNumber("tel:+1(123)456-7890")

        assertThat(extractedNumbers).containsExactly("+11234567890")
    }

    @Test
    fun `extracts the phone number when the raw string contains a phone number amidst other text`() {
        val extractedNumbers = helper.extractPhoneNumber(
            "To contact us, please call +1 123 456-7890 or write us an email at some@email.com"
        )

        assertThat(extractedNumbers).containsExactly("+11234567890")
    }
    
    @Test
    fun `extracts all phone numbers found in the raw string`() {
        val extractedNumbers = helper.extractPhoneNumber(
            "To contact us, please call +1 123 456-7890 or +1 987 (654) 3210"
        )

        assertThat(extractedNumbers).containsExactly("+11234567890", "+19876543210")
    }
}