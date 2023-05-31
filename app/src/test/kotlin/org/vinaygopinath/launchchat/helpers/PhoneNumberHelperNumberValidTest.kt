package org.vinaygopinath.launchchat.helpers

import com.google.common.truth.Truth.assertThat
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber.PhoneNumber
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


@RunWith(JUnit4::class)
class PhoneNumberHelperNumberValidTest {

    private val phoneNumberUtil = mock<PhoneNumberUtil>()
    private val helper = PhoneNumberHelper(phoneNumberUtil)

    @Test
    fun `returns true when the phone number is valid in the given region`() {
        val somePhoneNumberString = "+254744444444"
        val someRegion = "KE"
        setUpMocks(somePhoneNumberString, someRegion, true)

        assertThat(helper.isPhoneNumberValid(somePhoneNumberString, someRegion)).isTrue()
    }

    @Test
    fun `returns false when the phone number is not valid in the given region`() {
        val somePhoneNumberString = "+234123456789"
        val someRegion = "NG"
        setUpMocks(somePhoneNumberString, someRegion, false)

        assertThat(helper.isPhoneNumberValid(somePhoneNumberString, someRegion)).isFalse()
    }

    private fun setUpMocks(somePhoneNumberString: String, someRegion: String, isValid: Boolean) {
        val mockPhoneNumber = mock<PhoneNumber>()
        whenever(phoneNumberUtil.parse(somePhoneNumberString, someRegion))
            .thenReturn(mockPhoneNumber)
        whenever(phoneNumberUtil.isValidNumber(mockPhoneNumber)).thenReturn(isValid)
    }
}