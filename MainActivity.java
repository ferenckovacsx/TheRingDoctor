package com.example.ferenckovacsx.theringdoctor;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ToggleButton callButton;
    Button selectPictureButton;
    EditText callerNameEdittext;
    EditText callerNumberEdittext;
    AutoCompleteTextView callRingtoneEdittext;
    AutoCompleteTextView callDelayEdittext;
    Switch vibrateSwitch;

    String callerNameString;
    String callerNumberString;
    String callerRingtoneString;
    String callerDelayString;
    Boolean vibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences callPreferences = getSharedPreferences("CALL_PREF", Context.MODE_PRIVATE);

        callButton = (ToggleButton) findViewById(R.id.call_button);
        selectPictureButton = (Button) findViewById(R.id.button_selectimage);
        callerNameEdittext = (EditText) findViewById(R.id.inputlayout_name);
        callerNumberEdittext = (EditText) findViewById(R.id.inputlayout_number);
        callRingtoneEdittext = (AutoCompleteTextView) findViewById(R.id.autocomplete_ringtone);
        callDelayEdittext = (AutoCompleteTextView) findViewById(R.id.autocomplete_delay);
        vibrateSwitch = (Switch) findViewById(R.id.switch_vibrate);

        ArrayAdapter<String> ringtoneAutocompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listRingtones());

        callRingtoneEdittext.setAdapter(ringtoneAutocompleteAdapter);
        callRingtoneEdittext.setKeyListener(null);
        callRingtoneEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });


        List<String> delaysArraylist = Arrays.asList(getResources().getStringArray(R.array.delay_intervals));

        ArrayAdapter<String> delayAutocompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, delaysArraylist);

        callDelayEdittext.setAdapter(delayAutocompleteAdapter);
        callDelayEdittext.setKeyListener(null);
        callDelayEdittext.setOnTouchListener(new View.OnTouchListener() {
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

                callerNameString = callerNameEdittext.getText().toString();
                callerNumberString = callerNumberEdittext.getText().toString();
                callerRingtoneString = callRingtoneEdittext.getText().toString();
                callerDelayString = callDelayEdittext.getText().toString();
                vibrate = vibrateSwitch.isChecked();

                SharedPreferences.Editor callPreferencesEditor = callPreferences.edit();
                callPreferencesEditor.putString("callerName", callerNameString);
                callPreferencesEditor.putString("callerNumber", callerNumberString);
                callPreferencesEditor.putString("callerRingtone", getSelectedRingtoneUri(callerRingtoneString));
                callPreferencesEditor.putString("callerDelay", getSelectedRingtoneUri(callerDelayString));
                callPreferencesEditor.putBoolean("vibrate", vibrate);

                callPreferencesEditor.apply();

                startCall();

            }
        });

        selectPictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // custom dialog
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_image_source_picker);
                dialog.setTitle("Pick an option");

                Button selectCameraButton = (Button) dialog.findViewById(R.id.button_camera);
                selectCameraButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent takePictureIntent = new Intent();
                        takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePictureIntent, 1);
                    }
                });

                Button selectGalleryButton = (Button) dialog.findViewById(R.id.button_gallery);
                selectGalleryButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //pick image from gallery
                        Intent pickFromGalleryIntent = new Intent();
                        pickFromGalleryIntent.setType("image/*");
                        pickFromGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(pickFromGalleryIntent, "Select Picture"), 1);
                    }
                });

                dialog.show();
            }
        });

        vibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (vibrateSwitch.isChecked()) {
                    vibrateSwitch.setTextColor(Color.parseColor("#1874A8"));
                } else {
                    vibrateSwitch.setTextColor(Color.parseColor("#55B8D8"));
                }
            }
        });

    }




    public ArrayList<String> listRingtones() {

        ArrayList<String> listOfRingtones = new ArrayList<>();
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

        if (selectedRingtone.equals("Default")) {
            selectedRingtoneUri = manager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        } else {
            manager.setType(RingtoneManager.TYPE_RINGTONE);
            Cursor cursor = manager.getCursor();
            while (cursor.moveToNext()) {
                String ringtoneTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                if (ringtoneTitle.equals(selectedRingtone)) {
                    selectedRingtoneUri = manager.getRingtoneUri(cursor.getPosition());
                }
            }
        }

        return selectedRingtoneUri.toString();
    }


    public void startCall() {

        int callDelay = 0;

        switch (callerDelayString) {
            case "Now":
                callDelay = 0;
                break;
            case "3 seconds":
                callDelay = 3000;
                break;
            case "5 seconds":
                callDelay = 5000;
                break;
            case "10 seconds":
                callDelay = 10000;
                break;
        }

        Intent intent = new Intent(this, CallBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + callDelay, pendingIntent);
        Toast.makeText(this, "Alarm set in " + callDelay + " seconds", Toast.LENGTH_LONG).show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
//
//            Uri uri = data.getData();
//
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                ImageView imageView = (ImageView) findViewById(R.id.rawImageViewMain);
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}

