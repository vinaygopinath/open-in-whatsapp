package org.vinaygopinath.launchchat

import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Before
import org.junit.Test
import org.vinaygopinath.launchchat.helpers.AssertionHelper.assertIntentNavigation

class MainActivityMenuTest {

    @Before
    fun setUp() {
        launch(MainActivity::class.java)
    }

    @Test
    fun launchesGithubPageOnAboutClick() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        assertIntentNavigation(Intent.ACTION_VIEW, Constants.GITHUB_REPO_URL) {
            onView(withText("About")).perform(click())
        }
    }
}