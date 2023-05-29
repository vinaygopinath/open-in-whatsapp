package org.vinaygopinath.openinchat.fragment

import android.app.Activity
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import com.github.ialokim.phonefield.PhoneInputLayout
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.vinaygopinath.openinchat.R
import org.vinaygopinath.openinchat.prefs.Prefs
import java.io.UnsupportedEncodingException
import java.net.URISyntaxException
import java.net.URLEncoder

class MainFragment : Fragment() {
    // View elements
    lateinit var pickBtn: Button
    lateinit var mPhoneInput: PhoneInputLayout
    lateinit var shareMsg: EditText
    lateinit var shareBtn: Button
    lateinit var paste: ImageView
    lateinit var mBtnLink: TextView

    // State
    private var isShare = false
    private var number: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requireActivity().intent.action === "org.vinaygopinath.openinchat.CONTACTS") {
            pick()
        }
    }

    override fun onStart() {
        val intent = requireActivity().intent
        val action = intent.action
        if (Intent.ACTION_SEND == action) {
            val type = intent.type
            if ("text/x-vcard" == type) {
                isShare = true
                val contactUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                val cr: ContentResolver
                cr =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requireContext().contentResolver else requireActivity().contentResolver
                var data = ""
                try {
                    val stream = cr.openInputStream(contactUri!!)
                    val fileContent = StringBuffer("")
                    var ch: Int
                    while (stream!!.read().also { ch = it } != -1) fileContent.append(ch.toChar())
                    stream.close()
                    data = String(fileContent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
//                for (line in data.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
//                    .toTypedArray()) {
//                    line = line.trim { it <= ' ' }
//                    //todo: support other phone numbers from vcard
//                    if (line.startsWith("TEL;CELL:")) {
//                        number = line.substring(9)
//                        mPhoneInput.setPhoneNumber(number)
//                    }
//                }
            }
        } else if (Intent.ACTION_DIAL == action) {
            number = intent.data.toString().substring(3)
            mPhoneInput.setPhoneNumber(number)
        }
        super.onStart()
    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
////        val rootView = inflater.inflate(R.layout.fragment, container, false)
//        // baseActivityContext = container.getContext();
////        initUI(rootView)
////        pickBtn = rootView.findViewById(R.id.choose_from_contacts_button)
////        pickBtn.setOnClickListener { pick() }
////        return rootView
//    }

    private fun initUI(rootView: View) {
        mBtnLink = rootView.findViewById(R.id.open_whatsapp_button)
        shareMsg = rootView.findViewById(R.id.message_input)
        paste = rootView.findViewById(R.id.paste_from_clipboard_button)
        mBtnLink.setOnClickListener { open() }
        shareBtn.setOnClickListener { share() }
        paste.setOnClickListener { setNumberFromClipBoard() }
        mPhoneInput.setDefaultCountry(Prefs(requireContext()).lastRegion)
        mPhoneInput.editText.imeOptions = EditorInfo.IME_ACTION_SEND
        mPhoneInput.editText
            .setImeActionLabel(getString(R.string.button_description_open_whatsapp), EditorInfo.IME_ACTION_SEND)
        mPhoneInput.editText
            .setOnEditorActionListener(OnEditorActionListener { v, actionId, event -> //isFromClipBoard = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    open()
                    return@OnEditorActionListener true
                }
                false
            })
    }

    private fun setNumberFromClipBoard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val clipboardManager =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requireContext().getSystemService(
                    Context.CLIPBOARD_SERVICE
                ) as ClipboardManager else (requireView().context.getSystemService(
                    Context.CLIPBOARD_SERVICE
                ) as ClipboardManager)
            val clipData = clipboardManager.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val item = clipData.getItemAt(0)
                val text = item.text.toString()
                //text='+'+text.replaceAll("/[^0-9]/", "");
                mPhoneInput.setPhoneNumber(text)
                // isFromClipBoard = true;
            } else {
                Toast.makeText(context, R.string.empty_clipboard, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validate(): String? {
        // return mPhoneInput.getPhoneNumberE164();
        return if (mPhoneInput.isValid) mPhoneInput.phoneNumberE164 else null
    }

    private val shareMSG: String
        get() = try {
            "text=" + URLEncoder.encode(shareMsg.text.toString(), "utf-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            ""
        }

    private fun hideKeyboard(v: View?) {
        val imm =
            v!!.context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun open() {
        if (setNumber()) openInWhatsapp()
    }

    private fun share() {
        if (setNumber()) shareLink(shareMSG)
    }

    private fun pick() {
        startActivityForResult(
            Intent(Intent.ACTION_PICK)
                .setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE), PICK_CONTACT
        )
    }

    private fun setNumber(): Boolean {
        hideKeyboard(mPhoneInput)
        mPhoneInput.setError(null)
        // if (!isFromClipBoard)
        number = validate()
        if (number == null) {
            mPhoneInput.setError(getString(R.string.label_error_incorrect_phone))
            return false
        }
        storeCountryCode()
        return true
    }

    private fun storeCountryCode() {
        // Store country code
        if (mPhoneInput.isValid) {
            val phoneUtil = PhoneNumberUtil.createInstance(requireContext())
            try {
                val phoneNumber = phoneUtil.parse(mPhoneInput.phoneNumberE164, "")
                Prefs(requireContext()).lastRegion = phoneUtil.getRegionCodeForNumber(phoneNumber)
            } catch (e: NumberParseException) {
                Log.e(
                    "OpenInChat",
                    "Failed to store country code. NumberParseException thrown while trying to parse " + mPhoneInput.phoneNumberE164
                )
            }
        }
    }

    private fun getNumber(): String {
        return if (number!!.isEmpty()) {
            ""
        } else "phone=" + number!!.replace("^0+".toRegex(), "")
    }

    private fun openInWhatsapp() {
        try {
            startActivity(Intent.parseUri("whatsapp://send/?" + getNumber(), 0))
        } catch (ignore: URISyntaxException) {
            ignore.printStackTrace()
        } catch (e: ActivityNotFoundException) {
//            Snackbar.make(this.getView(), R.string.label_error_whatsapp_not_installed, Snackbar.LENGTH_LONG).show();
        }
    }

    private fun shareLink(message: String) {
        val number = getNumber()
        val append = StringBuilder().append("https://api.whatsapp.com/send?").append(number)
        val str = if (number.isEmpty() || message.isEmpty()) "" else "&"
        val url = append.append(str).append(message).toString()
        val intent = Intent("android.intent.action.SEND")
        intent.putExtra("android.intent.extra.TEXT", url)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Send to "))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                val contactUri = data!!.data
                val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                var cursor: Cursor? = null
                cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requireContext().contentResolver.query(
                        contactUri!!, projection,
                        null, null, null
                    )
                } else {
                    requireActivity().contentResolver.query(
                        contactUri!!, projection,
                        null, null, null
                    )
                }
                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    val numberIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val number = cursor.getString(numberIndex)
                    mPhoneInput.setPhoneNumber(number)
                }
                cursor?.close()
            }
        }
    }

    companion object {
        private const val PICK_CONTACT = 1
    }
}