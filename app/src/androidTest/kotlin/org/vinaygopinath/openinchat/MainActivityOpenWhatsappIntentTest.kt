package org.vinaygopinath.openinchat

import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.vinaygopinath.openinchat.helpers.IntentHelper

class MainActivityOpenWhatsappIntentTest {

    @Before
    fun setUp() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun launchesWhatsappChatWithTheEnteredNumber() {
        val phoneNumber = "+1555555555"
        onView(withId(R.id.phone_number_input)).perform(typeText(phoneNumber))
        val generatedUrl = IntentHelper().generateWhatsappUrl(phoneNumber, null)

        Intents.init()
        val expectedIntent = Matchers.allOf(
            IntentMatchers.hasAction(Intent.ACTION_VIEW),
            IntentMatchers.hasData(generatedUrl)
        )

        Intents.intending(expectedIntent).respondWith(
            Instrumentation.ActivityResult(0, null)
        )

        onView(withId(R.id.open_whatsapp_button)).perform(click())

        Intents.intended(expectedIntent)
    }

    @Test
    fun launchesWhatsappChatWithTheEnteredNumberAndMessage() {
        val phoneNumber = "+1555555555"
        val someMessage = "Hi!"
        onView(withId(R.id.phone_number_input)).perform(typeText(phoneNumber))
        val generatedUrl = IntentHelper().generateWhatsappUrl(phoneNumber, someMessage)

        Intents.init()
        val expectedIntent = Matchers.allOf(
            IntentMatchers.hasAction(Intent.ACTION_VIEW),
            IntentMatchers.hasData(generatedUrl)
        )

        Intents.intending(expectedIntent).respondWith(
            Instrumentation.ActivityResult(0, null)
        )

        onView(withId(R.id.open_whatsapp_button)).perform(click())

        Intents.intended(expectedIntent)
    }
}