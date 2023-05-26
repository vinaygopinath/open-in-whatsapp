package org.vinaygopinath.openinchat.fragment;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.ialokim.phonefield.PhoneInputLayout;

import org.vinaygopinath.openinchat.R;
import org.vinaygopinath.openinchat.prefs.Prefs;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class MainFragment extends Fragment {

    private static int PICK_CONTACT = 1;
    protected Button pickBtn;
    protected boolean isShare = false;
    protected PhoneInputLayout mPhoneInput;
    protected EditText shareMsg;
    protected Button shareBtn;
    protected TextView mBtnLink;
   // private boolean isFromClipBoard;
    private ImageView paste;
    private String number;

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getIntent().getAction() == "org.vinaygopinath.openinchat.org.vinaygopinath.openinchat.CONTACTS") {
            pick();
        }
    }

    @Override
    public void onStart() {
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            String type = intent.getType();
            if ("text/x-vcard".equals(type)) {
                isShare = true;
                Uri contactUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                ContentResolver cr;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    cr = this.getContext().getContentResolver();
                else cr = getActivity().getContentResolver();

                String data = "";
                try {
                    InputStream stream = cr.openInputStream(contactUri);
                    StringBuffer fileContent = new StringBuffer("");
                    int ch;
                    while ((ch = stream.read()) != -1)
                        fileContent.append((char) ch);
                    stream.close();
                    data = new String(fileContent);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (String line : data.split("\n")) {
                    line = line.trim();
                    //todo: support other phone numbers from vcard
                    if (line.startsWith("TEL;CELL:")) {
                        number = line.substring(9);
                        mPhoneInput.setPhoneNumber(number);
                    }
                }
            }
        } else if (Intent.ACTION_DIAL.equals(action)) {
            number = intent.getData().toString().substring(3);
            Log.d(MainFragment.class.getName(), "onStart: number==" + number);
            mPhoneInput.setPhoneNumber(number);
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment, container, false);
        // baseActivityContext = container.getContext();
        initUI(rootView);
        pickBtn = rootView.findViewById(R.id.btn_pick);
        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void initUI(View rootView) {
        mPhoneInput = rootView.findViewById(R.id.phone_input_layout);
        mBtnLink = rootView.findViewById(R.id.btn_send);
        shareMsg = rootView.findViewById(R.id.msg_text);
        shareBtn = rootView.findViewById(R.id.btn_share);
        paste = rootView.findViewById(R.id.btn_paste);

        mBtnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNumberFromClipBoard();

            }
        });

        mPhoneInput.setDefaultCountry(new Prefs(getContext()).getLastRegion());
        mPhoneInput.getEditText().setImeOptions(EditorInfo.IME_ACTION_SEND);
        mPhoneInput.getEditText().setImeActionLabel(getString(R.string.label_send), EditorInfo.IME_ACTION_SEND);

        mPhoneInput.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //isFromClipBoard = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    open();
                    return true;
                }
                return false;
            }
        });

    }

    private void setNumberFromClipBoard() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ClipboardManager clipboardManager = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ? (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE) : (ClipboardManager) getView().getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                ClipData.Item item = clipData.getItemAt(0);
                String text = item.getText().toString();
                //text='+'+text.replaceAll("/[^0-9]/", "");
                mPhoneInput.setPhoneNumber(text);
               // isFromClipBoard = true;
            } else {
                Toast.makeText(getContext(), R.string.empty_clipboard, Toast.LENGTH_SHORT).show();
            }
        }

    }


    protected String validate() {
        // return mPhoneInput.getPhoneNumberE164();
        return mPhoneInput.isValid() ? mPhoneInput.getPhoneNumberE164() : null;
    }

    protected String getShareMSG() {
        try {
            return "text=" + URLEncoder.encode(shareMsg.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    protected void open() {
        if (setNumber())
            openInWhatsapp();
    }


    protected void share() {
        if (setNumber())
            shareLink(getShareMSG());
    }

    protected void pick() {
        startActivityForResult(new Intent(Intent.ACTION_PICK)
                .setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE), PICK_CONTACT);
    }

    private boolean setNumber() {
        hideKeyboard(mPhoneInput);
        mPhoneInput.setError(null);
        // if (!isFromClipBoard)
        number = validate();
        if (number == null) {
            mPhoneInput.setError(getString(R.string.label_error_incorrect_phone));
            return false;
        }

        storeCountryCode();

        return true;
    }

    private void storeCountryCode() {
        // Store country code
        if (mPhoneInput.isValid()) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(requireContext());
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(mPhoneInput.getPhoneNumberE164(), "");
                new Prefs(requireContext()).setLastRegion(phoneUtil.getRegionCodeForNumber(phoneNumber));
            } catch (NumberParseException e) {
                Log.e("OpenInChat", "Failed to store country code. NumberParseException thrown while trying to parse " + mPhoneInput.getPhoneNumberE164());
            }
        }

    }

    private String getNumber() {
        if (this.number.length() == 0) {
            return "";
        }
        return "phone=" + this.number.replaceAll("^0+", "");
    }

    private void openInWhatsapp() {
        try {
            startActivity(Intent.parseUri("whatsapp://send/?" + getNumber(), 0));
        } catch (URISyntaxException ignore) {
            ignore.printStackTrace();
        } catch (ActivityNotFoundException e) {
//            Snackbar.make(this.getView(), R.string.label_error_whatsapp_not_installed, Snackbar.LENGTH_LONG).show();
        }
    }

    private void shareLink(String message) {
        String number = getNumber();
        StringBuilder append = new StringBuilder().append("http://api.whatsapp.com/send?").append(number);
        String str = (number.length() == 0 || message.length() == 0) ? "" : "&";
        String url = append.append(str).append(message).toString();
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", url);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Send to "));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cursor = getContext().getContentResolver().query(contactUri, projection,
                            null, null, null);
                } else {
                    cursor = getActivity().getContentResolver().query(contactUri, projection,
                            null, null, null);
                }
                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIndex);
                    mPhoneInput.setPhoneNumber(number);
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }
}
