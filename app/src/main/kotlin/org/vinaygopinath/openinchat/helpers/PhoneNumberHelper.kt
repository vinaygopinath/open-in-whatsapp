package org.vinaygopinath.openinchat.helpers

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import javax.inject.Inject

class PhoneNumberHelper @Inject constructor(private val phoneNumberUtil: PhoneNumberUtil) {

    fun isPhoneNumberValid(phoneNumberString: String, regionCode: String): Boolean {
        val phoneNumber = phoneNumberUtil.parse(phoneNumberString, regionCode)
        return phoneNumberUtil.isValidNumber(phoneNumber)
    }
}