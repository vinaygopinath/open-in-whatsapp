package org.vinaygopinath.launchchat

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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import org.vinaygopinath.launchchat.helpers.ClipboardHelper
import org.vinaygopinath.launchchat.helpers.IntentHelper
import org.vinaygopinath.launchchat.helpers.PhoneNumberHelper
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var phoneNumberHelper: PhoneNumberHelper

    @Inject
    lateinit var clipboardHelper: ClipboardHelper

    @Inject
    lateinit var intentHelper: IntentHelper

    private lateinit var phoneNumberInput: TextInputEditText
    private lateinit var phoneNumberInputLayout: TextInputLayout
    private lateinit var messageInput: EditText
    private lateinit var chooseContactButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeView()

        processIntent()
    }

    private fun initializeView() {
        phoneNumberInputLayout = findViewById(R.id.phone_number_input_layout)
        phoneNumberInput = findViewById(R.id.phone_number_input)
        messageInput = findViewById(R.id.message_input)
        findViewById<MaterialButton>(R.id.paste_from_clipboard_button).setOnClickListener {
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
        phoneNumberInputLayout.error = null
        val phoneNumbers = phoneNumberHelper.extractPhoneNumber(phoneNumberInput.text.toString())
        if (phoneNumbers.isEmpty()) {
            phoneNumberInputLayout.error = getString(R.string.toast_invalid_phone_number)
        } else if (phoneNumbers.size != 1) {
            // TODO Multiple phone numbers detected
        } else {
            val phoneNumber = phoneNumbers.first()
            val message = messageInput.text.toString().trim()
            try {
                startActivity(lambda(phoneNumber, message))
            } catch (e: ActivityNotFoundException) {
                showToast(errorToast)
            }
        }
    }

    private fun processIntent() {
        val processedIntent = intentHelper.processLaunchIntent(intent)
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

    private fun showToast(@StringRes toastResId: Int) {
        Toast.makeText(this, toastResId, Toast.LENGTH_LONG).show()
    }
}