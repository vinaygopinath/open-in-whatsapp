package org.vinaygopinath.launchchat.screens.main

import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.vinaygopinath.launchchat.R
import org.vinaygopinath.launchchat.helpers.AssertionHelper.assertIntentNavigation
import org.vinaygopinath.launchchat.helpers.IntentHelper

@HiltAndroidTest
class MainActivityPhoneNumberSelectionDialogTest {

    @get:Rule
    val rule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        launch(MainActivity::class.java)
    }

    @Test
    fun showsThePhoneNumberSelectionDialogTitleAndMessageWhenMultiplePhoneNumbersAreEntered() {
        val phoneNumbers = "+1555555555 +2663388373"
        onView(withId(R.id.phone_number_input)).perform(replaceText(phoneNumbers))

        onView(withId(R.id.open_whatsapp_button)).perform(click())

        onView(withText(R.string.phone_number_selection_dialog_title))
            .check(matches(isDisplayed()))

        onView(withText(R.string.phone_number_selection_dialog_message))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showsPhoneNumbersInThePhoneNumberSelectionDialogWhenMultiplePhoneNumbersAreEntered() {
        val firstPhoneNumber = "+1555555555"
        val secondPhoneNumber = "+2663388373"
        val phoneNumbers = "$firstPhoneNumber $secondPhoneNumber"
        onView(withId(R.id.phone_number_input)).perform(replaceText(phoneNumbers))

        onView(withId(R.id.open_whatsapp_button)).perform(click())

        onView(withText(firstPhoneNumber)).check(matches(withEffectiveVisibility(VISIBLE)))
        onView(withText(secondPhoneNumber)).check(matches(withEffectiveVisibility(VISIBLE)))
    }

    @Test
    fun launchesWhatsAppWhenAPhoneNumberInThePhoneNumberSelectionDialogIsSelected() {
        val firstPhoneNumber = "+1555555555"
        val secondPhoneNumber = "+2663388373"
        val phoneNumbers = "$firstPhoneNumber $secondPhoneNumber"
        onView(withId(R.id.phone_number_input)).perform(replaceText(phoneNumbers))

        onView(withId(R.id.open_whatsapp_button)).perform(click())

        val generatedUrl = IntentHelper().generateWhatsappUrl(firstPhoneNumber, null)
        assertIntentNavigation(Intent.ACTION_VIEW, generatedUrl) {
            onView(withText(firstPhoneNumber)).perform(click())
        }
    }
}