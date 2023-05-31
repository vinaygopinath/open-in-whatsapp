package org.vinaygopinath.launchchat

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
import org.vinaygopinath.launchchat.helpers.IntentHelper

class MainActivityOpenSignalIntentTest {

    @Before
    fun setUp() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun launchesSignalChatWithTheEnteredNumber() {
        val phoneNumber = "+1555555555"
        onView(withId(R.id.phone_number_input)).perform(typeText(phoneNumber))
        val generatedUrl = IntentHelper().generateSignalUrl(phoneNumber)

        Intents.init()
        val expectedIntent = Matchers.allOf(
            IntentMatchers.hasAction(Intent.ACTION_VIEW),
            IntentMatchers.hasData(generatedUrl)
        )

        Intents.intending(expectedIntent).respondWith(
            Instrumentation.ActivityResult(0, null)
        )

        onView(withId(R.id.open_signal_button)).perform(click())

        Intents.intended(expectedIntent)
    }
}