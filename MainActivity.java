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
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class MainActivity extends AppCompatActivity {

    ToggleButton callButton;
    ImageView selectPictureButton;
    ImageView imagePlaceholder;
    EditText nameAutoCompleteTextView;
    EditText numberAutoCompleteTextView;
    AutoCompleteTextView callerVoiceTextView;
    AutoCompleteTextView ringtoneAutoCompleteTextView;
    AutoCompleteTextView delayAutoCompleteTextView;
    Switch vibrateSwitch;
    AdView adview;

    String callerNameString;
    String callerNumberString;
    String callerRingtoneString;
    String callerVoiceString;
    String callerDelayString;
    Boolean vibrate;

    int customSelectedHour;
    int customSelectedMinute;

    String callerRingtoneUriString;

    Boolean isActivated;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        final SharedPreferences callPreferences = getSharedPreferences("CALL_PREF", Context.MODE_PRIVATE);

        intent = new Intent(this.getApplicationContext(), CallBroadcastReceiver.class);
        isActivated = (PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
        Log.d("mainact", "alarm is " + (isActivated ? "" : "not") + " working...");

        callButton = findViewById(R.id.call_button);
        selectPictureButton = findViewById(R.id.button_selectimage);
        nameAutoCompleteTextView = findViewById(R.id.inputlayout_name);
        numberAutoCompleteTextView = findViewById(R.id.inputlayout_number);
        callerVoiceTextView = findViewById(R.id.inputlayout_callervoice);
        ringtoneAutoCompleteTextView = findViewById(R.id.autocomplete_ringtone);
        delayAutoCompleteTextView = findViewById(R.id.autocomplete_delay);
        vibrateSwitch = findViewById(R.id.switch_vibrate);
        adview = findViewById(R.id.adView);
        imagePlaceholder = findViewById(R.id.image_placeholder);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.i("permissioncheck", "external: " + permissionCheck);

        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        final String croppedImageFilePath = getIntent().getStringExtra("croppedImageUri");
        Log.i("Mainactivity", "croppedImageFilePath: " + croppedImageFilePath);

        if (croppedImageFilePath != null) {
            File croppedImageFile = new File(croppedImageFilePath);
            Picasso.with(this)
                    .load(croppedImageFile)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imagePlaceholder);
        }

        List<String> callerVoiceList = Arrays.asList(getResources().getStringArray(R.array.caller_voice_array));
        ArrayAdapter<String> callerVoiceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, callerVoiceList);

        callerVoiceTextView.setAdapter(callerVoiceAdapter);
        callerVoiceTextView.setKeyListener(null);
        callerVoiceTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }


        });

//        callerVoiceTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_AppCompat);
//                dialog.setContentView(R.layout.dialog_try_for_free);
//                dialog.setTitle("Title...");
//
//                TextView tryForFreeTv = dialog.findViewById(R.id.tryForFreeTextview);
//                TextView upgradeTv = dialog.findViewById(R.id.upgradeTextView);
//                ImageView cancelIv = dialog.findViewById(R.id.cancelImageView);
//
//                cancelIv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.show();
//            }
//        });


        ArrayAdapter<String> ringtoneAutocompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listRingtones());

        ringtoneAutoCompleteTextView.setAdapter(ringtoneAutocompleteAdapter);
        ringtoneAutoCompleteTextView.setKeyListener(null);
        ringtoneAutoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("ringtoneACTV", "onTouch");
                ((AutoCompleteTextView) v).showDropDown();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        ringtoneAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        ArrayAdapter<String> delayAutocompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, delaysArraylist);

        delayAutoCompleteTextView.setAdapter(delayAutocompleteAdapter);
        delayAutoCompleteTextView.setKeyListener(null);
        delayAutoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }


        });

        delayAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                            delayAutoCompleteTextView.setText(currentTimeString, false);
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

                if (!isActivated) {
                    callerNameString = nameAutoCompleteTextView.getText().toString();
                    callerNumberString = numberAutoCompleteTextView.getText().toString();
                    callerRingtoneString = ringtoneAutoCompleteTextView.getText().toString();
                    callerDelayString = delayAutoCompleteTextView.getText().toString();
                    callerVoiceString = callerVoiceTextView.getText().toString();
                    vibrate = vibrateSwitch.isChecked();

                    SharedPreferences.Editor callPreferencesEditor = callPreferences.edit();
                    callPreferencesEditor.putString("callerName", callerNameString);
                    callPreferencesEditor.putString("callerNumber", callerNumberString);
                    callPreferencesEditor.putString("callerVoice", callerVoiceString);
                    callPreferencesEditor.putString("callerRingtone", getSelectedRingtoneUri(callerRingtoneString));
                    callPreferencesEditor.putString("callerImageFilePath", croppedImageFilePath);
                    callPreferencesEditor.putBoolean("vibrate", vibrate);

                    callPreferencesEditor.apply();

                    callButton.setChecked(true);
                    isActivated = true;
                    callHandler(true);
                } else {
                    callButton.setChecked(false);
                    isActivated = false;
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
                    vibrateSwitch.setTextColor(Color.parseColor("#1874A8"));
                    vibrateSwitch.setTypeface(null, Typeface.NORMAL);
                } else {
                    vibrateSwitch.setTextColor(Color.parseColor("#808080"));
                    vibrateSwitch.setTypeface(null, Typeface.ITALIC);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivated = (PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE) != null);

        if (!isActivated) {
            callButton.setChecked(false);
        }
    }

    public ArrayList<String> listRingtones() {

        ArrayList<String> listOfRingtones = new ArrayList<>();
        listOfRingtones.add("Select from device");
        listOfRingtones.add("Default");
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


    public void callHandler(boolean isActivated) {

        Log.i("Main", "callHandler: " + isActivated);


        if (isActivated) {

            Long triggerTime;

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

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, customSelectedHour);
                    calendar.set(Calendar.MINUTE, customSelectedMinute);
                    calendar.set(Calendar.SECOND, 0);

                    triggerTime = calendar.getTimeInMillis();
                    //triggerTime = calendarTimeMillis - System.currentTimeMillis();

                    Log.i("custom time", "delay: " + triggerTime);

            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            if (triggerTime == 0) {
                Toast.makeText(this, "Incoming fake call NOW!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incoming fake call in " + callerDelayString + ".", Toast.LENGTH_SHORT).show();
            }
        } else {
            PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT).cancel();
            Toast.makeText(this, "Fake call deactivated.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
            callerRingtoneUriString = audioUri.toString();
            audioCursor.close();

            ringtoneAutoCompleteTextView.setText(audioFileName, false);

        }
    }
}

