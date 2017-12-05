package com.example.ferenckovacsx.theringdoctor;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ferenckovacsx.theringdoctor.util.IabHelper;
import com.example.ferenckovacsx.theringdoctor.util.IabResult;
import com.example.ferenckovacsx.theringdoctor.util.Inventory;
import com.example.ferenckovacsx.theringdoctor.util.Purchase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.trialy.library.Trialy;
import io.trialy.library.TrialyCallback;

import static io.trialy.library.Constants.STATUS_TRIAL_JUST_ENDED;
import static io.trialy.library.Constants.STATUS_TRIAL_JUST_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_NOT_YET_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_OVER;
import static io.trialy.library.Constants.STATUS_TRIAL_RUNNING;

public class MainActivity extends AppCompatActivity {

    ToggleButton callButton;
    ImageView selectPictureButton;
    ImageView imagePlaceholder;
    EditText nameTv;
    EditText numberTv;
    AutoCompleteTextView voiceACTv;
    AutoCompleteTextView ringtoneACTv;
    AutoCompleteTextView delayACTv;
    TextView vibrateTv;
    Switch vibrateSwitch;
    AdView adview;

    String callerNameString;
    String callerNumberString;
    String callerRingtoneString;
    String callerVoiceString;
    String callerDelayString;
    Boolean vibrate;

    public static final String callerNameTag = "CALLER_NAME_TAG";
    public static final String callerNumberTag = "CALLER_NUMBER_TAG";
    public static final String callerRingtoneTag = "CALLER_RINGTONE_TAG";
    public static final String callerRingtoneUriTag = "CALLER_RINGTONE_URI_TAG";
    public static final String callerVoiceTag = "CALLER_VOICE_TAG";
    public static final String callerDelayTag = "CALLER_DELAY_TAG";
    public static final String callerVibrateTag = "CALLER_VIBRATE_TAG";

    int customSelectedHour;
    int customSelectedMinute;

    String callerRingtoneUriString;
    String voiceUriString;

    Boolean isRingerActivated;
    Boolean isPremium = false;
    Boolean isTrialActive = false;

    Intent ringerIntent;
    SharedPreferences callPreferences;

    Trialy mTrialy;
    IabHelper mHelper;

    final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq2f2oIb3qEXhhK5zb8gAV61Vg7ovE0HOQ1UpdYy/qk8sOUirht94cGZHu2Tj3NDwDT2cUiCWNIgriAJW4KLCUe8jV2rBGJjV04jyCiZkACEMNGJ+hrAUQaVzgFeclcvml4HUdXmLEvBmp8pAAaVrhC3cK+8RTni0dk7oyIJN7NunWvxLs7A77nYzp/CkA/eA/Godb6vDBXH3pu9QW6/LfvqfxjVuQDmexYCEZPialI7hTAHxBeqVKfjS4cJyqbYgwW4ETu/Yxzi0M8icAIS/V2+LhftQ5PXpZvtKyXMlVqWGKPMmU0/NbR+da0DQ9Jg0Mp0uIXuI/7FTMM/h9XBRGwIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            Log.i("MAIN", "savedInstanceState not null");
        } else {
            Log.i("MAIN", "savedInstanceState IS null");
        }


        callPreferences = getSharedPreferences("CALL_PREF", Context.MODE_PRIVATE);

        ringerIntent = new Intent(this.getApplicationContext(), CallBroadcastReceiver.class);
        isRingerActivated = (PendingIntent.getBroadcast(this.getApplicationContext(), 0, ringerIntent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
        Log.d("mainact", "alarm is " + (isRingerActivated ? "" : "not") + " working...");


        //UI ELEMENTS
        callButton = findViewById(R.id.call_button);
        selectPictureButton = findViewById(R.id.button_selectimage);
        nameTv = findViewById(R.id.inputlayout_name);
        numberTv = findViewById(R.id.inputlayout_number);
        voiceACTv = findViewById(R.id.inputlayout_callervoice);
        ringtoneACTv = findViewById(R.id.autocomplete_ringtone);
        delayACTv = findViewById(R.id.autocomplete_delay);
        vibrateTv = findViewById(R.id.switch_vibrate_text);
        vibrateSwitch = findViewById(R.id.switch_vibrate);
        adview = findViewById(R.id.adView);
        imagePlaceholder = findViewById(R.id.image_placeholder);

        //get saved data (if there is any)
        callerNameString = callPreferences.getString(callerNameTag, null);
        callerNumberString = callPreferences.getString(callerNumberTag, null);
        callerVoiceString = callPreferences.getString(callerVoiceTag, null);
        callerRingtoneString = callPreferences.getString(callerRingtoneTag, null);
        callerDelayString = callPreferences.getString(callerDelayTag, null);
        vibrate = callPreferences.getBoolean(callerVibrateTag, false);

        //update UI with saved data
        nameTv.setText(callerNameString);
        numberTv.setText(callerNumberString);
        voiceACTv.setText(callerVoiceString);
        ringtoneACTv.setText(callerRingtoneString);
        delayACTv.setText(callerDelayString);
        vibrateSwitch.setChecked(vibrate);

        //
        if (vibrateSwitch.isChecked()) {
            vibrateTv.setTextColor(Color.parseColor("#1874A8"));
            vibrateTv.setTypeface(null, Typeface.NORMAL);
        } else {
            final ColorStateList hintColor = nameTv.getHintTextColors();
            vibrateTv.setTextColor(hintColor);
            vibrateTv.setTypeface(null, Typeface.ITALIC);
        }

        //TRIALY SETUP
        mTrialy = new Trialy(this, "TW9RRQYD69UTQ2OKR0T");
        mTrialy.checkTrial("default", mTrialyCallback);

        //AD BANNER SETUP
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        //IN-APP BILLING SETUP
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d("IabHelper", "Problem setting up In-app Billing: " + result);
                }
            }
        });

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.i("permissioncheck", "external: " + permissionCheck);


        final String croppedImageFilePath = getIntent().getStringExtra("croppedImageUri");
        Log.i("Mainactivity", "croppedImageFilePath: " + croppedImageFilePath);

        if (croppedImageFilePath != null) {
            File croppedImageFile = new File(croppedImageFilePath);
            Picasso.with(this)
                    .load(croppedImageFile)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imagePlaceholder);
        }

        Log.i("trial", "current status: " + isTrialActive);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d("IAB_setup", "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d("IAB_setup", "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d("IAB_setup", "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.d("IAB_setup", "Error querying inventory. Another async operation in progress.");
                }
            }
        });

        //Premium user or trial is active
        if (isTrialActive != null) {
            if (isTrialActive || isPremium) {
                List<String> callerVoiceList = Arrays.asList(getResources().getStringArray(R.array.caller_voice_array));
                final ArrayAdapter<String> callerVoiceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, callerVoiceList) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 2) {
                            return false;
                        }
                        return true;
                    }
                };

                voiceACTv.setAdapter(callerVoiceAdapter);
                voiceACTv.setKeyListener(null);
                voiceACTv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        ((AutoCompleteTextView) v).showDropDown();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                        return false;
                    }


                });

                voiceACTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selected = (String) parent.getItemAtPosition(position);

                        Log.i("voiceACTV", "onItemClick pos: " + selected);

                        if (selected.equals("Select from device")) {

                            Intent pickAudioIntent = new Intent();
                            pickAudioIntent.setType("audio/*");
                            pickAudioIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(pickAudioIntent, "Select from file..."), 3);
                        }
                    }
                });
            }

            //Trial period has ended and not premium user
            if (!isTrialActive && !isPremium) {

                voiceACTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_AppCompat);
                        dialog.setContentView(R.layout.dialog_try_for_free);

                        TextView trialInfoTv = dialog.findViewById(R.id.trial_info);
                        TextView tryForFreeTv = dialog.findViewById(R.id.tryForFreeTextview);
                        TextView upgradeTv = dialog.findViewById(R.id.upgradeTextView);
                        TextView upgradeTvCenter = dialog.findViewById(R.id.upgradeTextViewCenter);
                        ImageView cancelIv = dialog.findViewById(R.id.cancelImageView);

                        tryForFreeTv.setVisibility(View.GONE);
                        upgradeTv.setVisibility(View.GONE);
                        upgradeTvCenter.setVisibility(View.VISIBLE);
                        trialInfoTv.setText(getResources().getText(R.string.trial_ended));

                        upgradeTvCenter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    mHelper.launchPurchaseFlow(MainActivity.this, "voice_call_pack", 10001,
                                            mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                                } catch (IabHelper.IabAsyncInProgressException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        cancelIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });
            }
        }


        //No premium user. Trial not started yet
        if (isTrialActive == null && !isPremium) {

            voiceACTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_AppCompat);
                    dialog.setContentView(R.layout.dialog_try_for_free);

                    TextView tryForFreeTv = dialog.findViewById(R.id.tryForFreeTextview);
                    TextView upgradeTv = dialog.findViewById(R.id.upgradeTextView);
                    ImageView cancelIv = dialog.findViewById(R.id.cancelImageView);


                    tryForFreeTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTrialy.startTrial("default", mTrialyCallback);
                            dialog.dismiss();
                        }
                    });

                    upgradeTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                mHelper.launchPurchaseFlow(MainActivity.this, "voice_call_pack", 10001,
                                        mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                            } catch (IabHelper.IabAsyncInProgressException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    cancelIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }


        ArrayAdapter<String> ringtoneAutocompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listRingtones()) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 2) {
                    return false;
                }
                return true;
            }
        };

        ringtoneACTv.setAdapter(ringtoneAutocompleteAdapter);
        ringtoneACTv.setKeyListener(null);
        ringtoneACTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("ringtoneACTV", "onTouch");
                ((AutoCompleteTextView) v).showDropDown();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        ringtoneACTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);

                if (selected.equals("Select from device")) {
                    Intent pickAudioIntent = new Intent();
                    pickAudioIntent.setType("audio/*");
                    pickAudioIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(pickAudioIntent, "Select from file..."), 2);
                }
            }
        });


        List<String> delaysArraylist = Arrays.asList(getResources().getStringArray(R.array.delay_intervals));
        ArrayAdapter<String> delayAutocompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, delaysArraylist) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 1) {
                    return false;
                }
                return true;
            }
        };

        delayACTv.setAdapter(delayAutocompleteAdapter);
        delayACTv.setKeyListener(null);
        delayACTv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        delayACTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selected = (String) parent.getItemAtPosition(position);
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                if (selected.equals(getResources().getString(R.string.custom_time))) {

                    final TimePickerDialog timePicker;
                    timePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            customSelectedHour = selectedHour;
                            customSelectedMinute = selectedMinute;

                            String currentTimeString = String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute);
                            delayACTv.setText(currentTimeString, false);
                        }
                    }, hour, minute, true);
                    timePicker.setTitle("Select Time");
                    timePicker.setCancelable(false);
                    timePicker.setCanceledOnTouchOutside(false);
                    timePicker.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            timePicker.getButton(Dialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                        }
                    });
                    timePicker.show();
                }
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!isRingerActivated) {
                    callerNameString = nameTv.getText().toString();
                    callerNumberString = numberTv.getText().toString();
                    callerRingtoneString = ringtoneACTv.getText().toString();
                    callerDelayString = delayACTv.getText().toString();
                    callerVoiceString = voiceACTv.getText().toString();
                    vibrate = vibrateSwitch.isChecked();

                    SharedPreferences.Editor callPreferencesEditor = callPreferences.edit();
                    callPreferencesEditor.putString(callerNameTag, callerNameString);
                    callPreferencesEditor.putString(callerNumberTag, callerNumberString);
//                    callPreferencesEditor.putString(callerVoiceTag, callerVoiceString);
                    callPreferencesEditor.putString(callerVoiceTag, getAudioAssetUri(callerVoiceString));
                    callPreferencesEditor.putString(callerRingtoneUriTag, getSelectedRingtoneUri(callerRingtoneString));
                    callPreferencesEditor.putString("callerImageFilePath", croppedImageFilePath);
                    callPreferencesEditor.putBoolean(callerVibrateTag, vibrate);

                    callPreferencesEditor.apply();

                    callButton.setChecked(true);
                    isRingerActivated = true;
                    callHandler(true);
                } else {
                    callButton.setChecked(false);
                    isRingerActivated = false;
                    callHandler(false);
                }
            }
        });

        selectPictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Intent pickFromGalleryIntent = new Intent();
                pickFromGalleryIntent.setType("image/*");
                pickFromGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickFromGalleryIntent, "Select Picture"), 1);
            }

        });

        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (vibrateSwitch.isChecked()) {
                    vibrateTv.setTextColor(Color.parseColor("#1874A8"));
                    vibrateTv.setTypeface(null, Typeface.NORMAL);
                } else {
                    final ColorStateList hintColor = nameTv.getHintTextColors();
                    vibrateTv.setTextColor(hintColor);
                    vibrateTv.setTypeface(null, Typeface.ITALIC);
                }
            }
        });
    }

    private TrialyCallback mTrialyCallback = new TrialyCallback() {
        @Override
        public void onResult(int status, long timeRemaining, String sku) {
            switch (status) {
                case STATUS_TRIAL_JUST_STARTED:
                    isTrialActive = true;
                    break;
                case STATUS_TRIAL_RUNNING:
                    isTrialActive = true;
                    break;
                case STATUS_TRIAL_JUST_ENDED:
                    isTrialActive = false;
                    break;
                case STATUS_TRIAL_NOT_YET_STARTED:
                    isTrialActive = null;
                    break;
                case STATUS_TRIAL_OVER:
                    isTrialActive = false;
                    break;
            }
            Log.i("TRIALY", "Returned status: " + Trialy.getStatusMessage(status));
            Log.i("TRIALY", "Trial status string: " + isTrialActive);
            Log.i("TRIALY", "Time left from trial: " + timeRemaining);
        }

    };


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d("IAB_inventory", "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.d("IAB_inventory", "Failed to query inventory: " + result);
                return;
            }

            Log.d("IAB_inventory", "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase("voice_call_pack");
            isPremium = (premiumPurchase != null);
            Log.d("IAB_premium_checkv", "User is " + (isPremium ? "PREMIUM" : "NOT PREMIUM"));
            Log.d("IAB_inventory", "Initial inventory query finished; enabling main UI.");
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d("purchaseFail", "Error purchasing: " + result);
            } else if (purchase.getSku().equals("voice_call_pack")) {
                isPremium = true;
            }
        }
    };


    public ArrayList<String> listRingtones() {

        ArrayList<String> listOfRingtones = new ArrayList<>();
        listOfRingtones.add(getResources().getString(R.string.select_from_device));
        listOfRingtones.add("Default");
        listOfRingtones.add("▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔");
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        while (cursor.moveToNext()) {
            String ringtoneTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            listOfRingtones.add(ringtoneTitle);
            Log.i("ringtoneTitle", "" + ringtoneTitle);
        }

        return listOfRingtones;
    }

    public String getSelectedRingtoneUri(String selectedRingtone) {

        RingtoneManager manager = new RingtoneManager(this);

        Uri selectedRingtoneUri;

        if (selectedRingtone.equals("Default") || selectedRingtone.equals("")) {
            selectedRingtoneUri = manager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
            callerRingtoneUriString = selectedRingtoneUri.toString();

        } else {
            manager.setType(RingtoneManager.TYPE_RINGTONE);
            Cursor cursor = manager.getCursor();
            while (cursor.moveToNext()) {
                String ringtoneTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                if (ringtoneTitle.equals(selectedRingtone)) {
                    selectedRingtoneUri = manager.getRingtoneUri(cursor.getPosition());
                    callerRingtoneUriString = selectedRingtoneUri.toString();
                }
            }
        }

        return callerRingtoneUriString;
    }

    public String getAudioAssetUri(String assetName) {
        Log.i("getAUdioAsset", "URI: " + "file:///android_asset/" + assetName);
        return "file:///android_asset/" + assetName;
    }

    public void callHandler(boolean isActivated) {

        Log.i("Main", "callHandler: " + isActivated);


        if (isActivated) {

            Long triggerTime;
            Boolean isCustomTime = false;

            switch (callerDelayString) {
                case "Now":
                    triggerTime = System.currentTimeMillis();
                    break;
                case "5 seconds":
                    triggerTime = System.currentTimeMillis() + 5000L;
                    break;
                case "10 seconds":
                    triggerTime = System.currentTimeMillis() + 10000L;
                    break;
                case "30 seconds":
                    triggerTime = System.currentTimeMillis() + 30000L;
                    break;
                case "1 minute":
                    triggerTime = System.currentTimeMillis() + 60000L;
                    break;
                case "10 minutes":
                    triggerTime = System.currentTimeMillis() + 100000L;
                    break;
                default:

                    isCustomTime = true;

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, customSelectedHour);
                    calendar.set(Calendar.MINUTE, customSelectedMinute);
                    calendar.set(Calendar.SECOND, 0);

                    triggerTime = calendar.getTimeInMillis();
                    //triggerTime = calendarTimeMillis - System.currentTimeMillis();

                    Log.i("custom time", "delay: " + triggerTime);

            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, ringerIntent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            if (triggerTime == 0) {
                Toast.makeText(this, "Incoming fake call NOW!", Toast.LENGTH_SHORT).show();
            } else if (isCustomTime) {
                Toast.makeText(this, "Incoming fake call at " + callerDelayString + ".", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incoming fake call in " + callerDelayString + ".", Toast.LENGTH_SHORT).show();
            }
        } else {
            PendingIntent.getBroadcast(this.getApplicationContext(), 0, ringerIntent, PendingIntent.FLAG_ONE_SHOT).cancel();
            Toast.makeText(this, "Fake call deactivated.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mHelper == null) return;
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }


        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri imageUri = data.getData();

            Intent cropImageIntent = new Intent(MainActivity.this, CropImageActivity.class);
            cropImageIntent.putExtra("imageUri", imageUri.toString());
            startActivity(cropImageIntent);

        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri audioUri = data.getData();
            Cursor audioCursor = getContentResolver().query(audioUri, null, null, null, null);
            int nameIndex = audioCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            audioCursor.moveToFirst();
            String audioFileName = audioCursor.getString(nameIndex);
            voiceUriString = audioUri.toString();
            audioCursor.close();

            ringtoneACTv.setText(audioFileName, false);

        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null && data.getData() != null) {


            Uri audioUri = data.getData();
            Cursor audioCursor = getContentResolver().query(audioUri, null, null, null, null);
            int nameIndex = audioCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            audioCursor.moveToFirst();
            String audioFileName = audioCursor.getString(nameIndex);
            String audioUriString = audioUri.toString();
            audioCursor.close();

            AudioBean audioBean = new AudioBean(audioFileName, audioUriString);

            voiceACTv.setText(audioBean.getFileName(), false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MAIN", "onDestroy");
        try {
            if (mHelper != null) mHelper.dispose();
            mHelper = null;
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        isRingerActivated = (PendingIntent.getBroadcast(getApplicationContext(), 0, ringerIntent, PendingIntent.FLAG_NO_CREATE) != null);

        if (!isRingerActivated) {
            callButton.setChecked(false);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MAIN", "onPause");
        Log.i("MAIN", "onPause delay: " + delayACTv.getText().toString());
        Log.i("MAIN", "onPause vibrate state: " + vibrateSwitch.isChecked());

        SharedPreferences.Editor callPreferencesEditor = callPreferences.edit();
        callPreferencesEditor.putString(callerNameTag, nameTv.getText().toString());
        callPreferencesEditor.putString(callerNumberTag, numberTv.getText().toString());
        callPreferencesEditor.putString(callerVoiceTag, voiceACTv.getText().toString());
        callPreferencesEditor.putString(callerRingtoneTag, ringtoneACTv.getText().toString());
        callPreferencesEditor.putString(callerDelayTag, delayACTv.getText().toString());
        callPreferencesEditor.putBoolean(callerVibrateTag, vibrateSwitch.isChecked());
        callPreferencesEditor.apply();
    }


}

