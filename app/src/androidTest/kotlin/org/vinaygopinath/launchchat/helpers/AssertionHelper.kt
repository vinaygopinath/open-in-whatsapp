package org.vinaygopinath.launchchat.helpers

import android.app.Instrumentation
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import org.hamcrest.Matchers.allOf

object AssertionHelper {

    fun assertIntentNavigation(action: String, data: String, performAction: () -> Unit) {
        Intents.init()
        try {
            val expectedIntent = allOf(
                IntentMatchers.hasAction(action),
                IntentMatchers.hasData(data)
            )

            Intents.intending(expectedIntent).respondWith(
                Instrumentation.ActivityResult(0, null)
            )

            performAction()

            Intents.intended(expectedIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            Intents.release()
        }
    }
}