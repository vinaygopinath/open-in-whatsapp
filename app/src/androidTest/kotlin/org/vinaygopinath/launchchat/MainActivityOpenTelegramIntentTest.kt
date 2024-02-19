package org.vinaygopinath.launchchat

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Before
import org.junit.Test
import org.vinaygopinath.launchchat.helpers.AssertionHelper.assertIntentNavigation
import org.vinaygopinath.launchchat.helpers.IntentHelper

class MainActivityOpenTelegramIntentTest {

    @Before
    fun setUp() {
        launch(MainActivity::class.java)
    }

    @Test
    fun launchesTelegramChatWithTheEnteredNumber() {
        val phoneNumber = "+1555555555"
        onView(withId(R.id.phone_number_input)).perform(replaceText(phoneNumber))
        val generatedUrl = IntentHelper().generateTelegramUrl(phoneNumber)

        assertIntentNavigation(Intent.ACTION_VIEW, generatedUrl) {
            onView(withId(R.id.open_telegram_button)).perform(click())
        }
    }
}