package org.vinaygopinath.launchchat

import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Test

class MainActivityMenuTest {

    @Before
    fun setUp() {
        launch(MainActivity::class.java)
    }

    @Test
    fun launchesGithubPageOnAboutClick() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        Intents.init()
        val expectedIntent = allOf(
            IntentMatchers.hasAction(Intent.ACTION_VIEW),
            IntentMatchers.hasData(Constants.GITHUB_REPO_URL)
        )

        Intents.intending(expectedIntent).respondWith(
            Instrumentation.ActivityResult(0, null)
        )

        onView(withText("About")).perform(click())

        intended(expectedIntent)
    }
}