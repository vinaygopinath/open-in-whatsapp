package org.vinaygopinath.launchchat.screens.main

import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.vinaygopinath.launchchat.R
import org.vinaygopinath.launchchat.helpers.AssertionHelper.assertIntentNavigation
import org.vinaygopinath.launchchat.helpers.IntentHelper

@HiltAndroidTest
class MainActivityOpenWhatsappIntentTest {

    @get:Rule
    val rule = HiltAndroidRule(this)

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