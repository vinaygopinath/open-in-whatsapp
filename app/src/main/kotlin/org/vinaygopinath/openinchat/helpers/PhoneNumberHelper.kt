package org.vinaygopinath.openinchat.helpers

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import javax.inject.Inject

class PhoneNumberHelper @Inject constructor(private val phoneNumberUtil: PhoneNumberUtil) {

    private val phoneNumberRegex by lazy {
        Regex("(?:tel:)?(\\+?[\\d- ()]+)")
    }

    private val invalidPhoneNumberCharactersRegex by lazy {
        Regex("[-() ]*")
    }

    fun isPhoneNumberValid(phoneNumberString: String, regionCode: String): Boolean {
        val phoneNumber = phoneNumberUtil.parse(phoneNumberString, regionCode)
        return phoneNumberUtil.isValidNumber(phoneNumber)
    }

    fun extractPhoneNumber(rawString: String): List<String> {
        val matches = phoneNumberRegex.findAll(rawString)
        return matches.filter { matchResult -> matchResult.groupValues.size == 2 }
            .map { matchResult -> matchResult.groupValues[1] }
            .filter { match -> match.isNotBlank() }
            .map { match -> match.replace(invalidPhoneNumberCharactersRegex, "") }
            .toList()
    }
}