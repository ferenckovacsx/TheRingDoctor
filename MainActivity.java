package com.example.ferenckovacsx.theringdoctor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button callButton;
    EditText callerNameEdittext;
    EditText callerNumberEdittext;
    AutoCompleteTextView callRingtoneEdittext;
    AutoCompleteTextView callDelayEdittext;

    String callerNameString;
    String callerNumberString;
    String callerRingtoneString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences callPreferences = getSharedPreferences("CALL_PREF", Context.MODE_PRIVATE);

        callButton = (Button) findViewById(R.id.call_button);
        callerNameEdittext = (EditText) findViewById(R.id.inputlayout_name);
        callerNumberEdittext = (EditText) findViewById(R.id.inputlayout_number);
        callRingtoneEdittext = (AutoCompleteTextView) findViewById(R.id.autocomplete_ringtone);
        callDelayEdittext = (AutoCompleteTextView) findViewById(R.id.autocomplete_delay);


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

        ArrayAdapter<String> delayAutocompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listRingtones());

        callRingtoneEdittext.setAdapter(delayAutocompleteAdapter);
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





        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                callerNameString = callerNameEdittext.getText().toString();
                callerNumberString = callerNumberEdittext.getText().toString();
                callerRingtoneString = callRingtoneEdittext.getText().toString();

                SharedPreferences.Editor callPreferencesEditor = callPreferences.edit();
                callPreferencesEditor.putString("callerName", callerNameString);
                callPreferencesEditor.putString("callerNumber", callerNumberString);
                callPreferencesEditor.putString("callerRingtone", getSelectedRingtoneUri(callerRingtoneString));
                callPreferencesEditor.apply();

                startCall();

            }
        });
    }

    public ArrayList<String> listRingtones() {

        ArrayList<String> listOfRingtones = new ArrayList<>();
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

        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        while (cursor.moveToNext()) {
            String ringtoneTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            if (ringtoneTitle.equals(selectedRingtone)) {
                selectedRingtoneUri = manager.getRingtoneUri(cursor.getPosition());
            }
        }

        return selectedRingtoneUri.toString();
    }


    public void startCall() {

        Intent intent = new Intent(this, CallBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000, pendingIntent);
        Toast.makeText(this, "Alarm set in " + 5 + " seconds", Toast.LENGTH_LONG).show();
    }


}

