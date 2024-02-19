package org.vinaygopinath.launchchat

import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Before
import org.junit.Test
import org.vinaygopinath.launchchat.helpers.AssertionHelper.assertIntentNavigation
import org.vinaygopinath.launchchat.helpers.IntentHelper

class MainActivityOpenWhatsappIntentTest {

    @Before
    fun setUp() {
        launch(MainActivity::class.java)
    }

    @Test
    fun launchesWhatsappChatWithTheEnteredNumber() {
        val phoneNumber = "+1555555555"
        onView(withId(R.id.phone_number_input)).perform(typeText(phoneNumber))
        val generatedUrl = IntentHelper().generateWhatsappUrl(phoneNumber, null)

        assertIntentNavigation(Intent.ACTION_VIEW, generatedUrl) {
            onView(withId(R.id.open_whatsapp_button)).perform(click())
        }
    }

    @Test
    fun launchesWhatsappChatWithTheEnteredNumberAndMessage() {
        val phoneNumber = "+1555555555"
        val someMessage = "Hi!"
        onView(withId(R.id.phone_number_input)).perform(replaceText(phoneNumber))
        onView(withId(R.id.message_input)).perform(replaceText(someMessage))
        val generatedUrl = IntentHelper().generateWhatsappUrl(phoneNumber, someMessage)

        assertIntentNavigation(Intent.ACTION_VIEW, generatedUrl) {
            onView(withId(R.id.open_whatsapp_button)).perform(click())
        }
    }
}