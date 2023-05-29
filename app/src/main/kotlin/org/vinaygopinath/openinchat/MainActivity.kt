package org.vinaygopinath.openinchat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.vinaygopinath.openinchat.helpers.ClipboardHelper
import org.vinaygopinath.openinchat.helpers.PhoneNumberHelper
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var phoneNumberHelper: PhoneNumberHelper

    @Inject
    lateinit var clipboardHelper: ClipboardHelper

    private lateinit var phoneNumberInput: EditText
    private lateinit var messageInput: EditText
    private lateinit var clipboardPasteButton: ImageButton
    private lateinit var chooseContactButton: ImageButton
    private lateinit var openInWhatsappButton: Button
    private lateinit var openInSignalButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeView()
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
        openInWhatsappButton = findViewById(R.id.open_whatsapp_button)
        openInSignalButton = findViewById(R.id.open_signal_button)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_about) {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/subhamtyagi/openinwa")))
        }
        return super.onOptionsItemSelected(item)
    }
}