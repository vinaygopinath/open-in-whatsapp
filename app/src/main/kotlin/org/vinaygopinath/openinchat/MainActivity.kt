package org.vinaygopinath.openinchat

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.vinaygopinath.openinchat.helpers.ClipboardHelper
import org.vinaygopinath.openinchat.helpers.IntentHelper
import org.vinaygopinath.openinchat.helpers.PhoneNumberHelper
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var phoneNumberHelper: PhoneNumberHelper

    @Inject
    lateinit var clipboardHelper: ClipboardHelper

    @Inject
    lateinit var intentHelper: IntentHelper

    private lateinit var phoneNumberInput: EditText
    private lateinit var messageInput: EditText
    private lateinit var chooseContactButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeView()

        processIntent()
    }

    private fun initializeView() {
        phoneNumberInput = findViewById(R.id.phone_number_input)
        messageInput = findViewById(R.id.message_input)
        findViewById<ImageButton>(R.id.paste_from_clipboard_button).setOnClickListener {
            val content = clipboardHelper.readClipboardContent()
            if (content is ClipboardHelper.ClipboardContent.ClipboardData) {
                phoneNumberInput.setText(content.content)
            }
        }
        chooseContactButton = findViewById(R.id.choose_from_contacts_button)
        findViewById<Button>(R.id.open_whatsapp_button).setOnClickListener {
            startActivityOrShowToast(R.string.toast_whatsapp_not_installed) { number, message ->
                intentHelper.getOpenWhatsappIntent(number, message.ifBlank { null })

            }
        }
        findViewById<Button>(R.id.open_signal_button).setOnClickListener {
            startActivityOrShowToast(R.string.toast_signal_not_installed) { phoneNumber, _ ->
                intentHelper.getOpenSignalIntent(phoneNumber)
            }
        }
        findViewById<Button>(R.id.open_telegram_button).setOnClickListener {
            startActivityOrShowToast(R.string.toast_telegram_not_installed) { phoneNumber, _ ->
                intentHelper.getOpenTelegramIntent(phoneNumber)
            }
        }
    }

    private fun startActivityOrShowToast(
        @StringRes errorToast: Int,
        lambda: (phoneNumber: String, message: String) -> Intent
    ) {
        val phoneNumber = phoneNumberInput.text.toString().trim() // TODO Get and process phone number
        val message = messageInput.text.toString().trim()
        try {
            startActivity(lambda(phoneNumber, message))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, errorToast, Toast.LENGTH_LONG).show()
        }
    }

    private fun processIntent() {
        val processedIntent = intentHelper.processIntent(intent)
        if (processedIntent is IntentHelper.ProcessedIntent.TelUriScheme) {
            phoneNumberInput.setText(processedIntent.phoneNumber)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            startActivity(intentHelper.getGithubRepoIntent())
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}