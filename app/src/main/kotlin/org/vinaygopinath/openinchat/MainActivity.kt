package org.vinaygopinath.openinchat

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
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
            try {
                val message = messageInput.text.toString()
                startActivity(intentHelper.getOpenWhatsappIntent(
                    phoneNumber = phoneNumberInput.text.toString(),
                    message = message.ifBlank { null }
                ))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Neither WhatsApp nor a browser is installed", Toast.LENGTH_LONG).show()
            }
        }
        findViewById<Button>(R.id.open_signal_button).setOnClickListener {
            try {
                startActivity(intentHelper.getOpenSignalIntent(
                    phoneNumber = phoneNumberInput.text.toString()
                ))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Neither Signal nor a browser app is installed", Toast.LENGTH_LONG).show()
            }
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