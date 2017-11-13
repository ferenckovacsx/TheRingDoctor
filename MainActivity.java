package com.example.ferenckovacsx.theringdoctor;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ToggleButton callButton;
    ImageView selectPictureButton;
    ImageView imagePlaceholder;
    EditText nameAutoCompleteTextView;
    EditText numberAutoCompleteTextView;
    AutoCompleteTextView callerVoice;
    AutoCompleteTextView ringtoneAutoCompleteTextView;
    AutoCompleteTextView delayAutoCompleteTextView;
    Switch vibrateSwitch;
    AdView adview;

    String callerNameString;
    String callerNumberString;
    String callerRingtoneString;
    String callerDelayString;
    Boolean vibrate;

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
        callerVoice = findViewById(R.id.inputlayout_callervoice);
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

        callerVoice.setAdapter(callerVoiceAdapter);
        callerVoice.setKeyListener(null);
        callerVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }


        });

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
                    startActivityForResult(Intent.createChooser(pickAudioIntent, "Select ringtone audio file"), 2);
                }


                int pos = Arrays.asList(listRingtones()).indexOf(selected);
                Log.i("autocomplete_itemclick", "selected: " + selected);
                Log.i("autocomplete_itemclick", "pos: " + pos);


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


        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!isActivated) {
                    callerNameString = nameAutoCompleteTextView.getText().toString();
                    callerNumberString = numberAutoCompleteTextView.getText().toString();
                    callerRingtoneString = ringtoneAutoCompleteTextView.getText().toString();
                    callerDelayString = delayAutoCompleteTextView.getText().toString();
                    vibrate = vibrateSwitch.isChecked();

                    SharedPreferences.Editor callPreferencesEditor = callPreferences.edit();
                    callPreferencesEditor.putString("callerName", callerNameString);
                    callPreferencesEditor.putString("callerNumber", callerNumberString);
                    callPreferencesEditor.putString("callerRingtone", getSelectedRingtoneUri(callerRingtoneString));
//                    callPreferencesEditor.putString("callerRingtone", callerRingtoneUriString);
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
//                });

//                dialog.show();
//            }
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

        Uri selectedRingtoneUri = manager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);

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

            int callDelay = 0;

            switch (callerDelayString) {
                case "Now":
                    callDelay = 0;
                    break;
                case "5 seconds":
                    callDelay = 5000;
                    break;
                case "10 seconds":
                    callDelay = 10000;
                    break;
                case "30 seconds":
                    callDelay = 30000;
                    break;
                case "1 minute":
                    callDelay = 60000;
                    break;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + callDelay, pendingIntent);
            if (callDelay == 0) {
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

