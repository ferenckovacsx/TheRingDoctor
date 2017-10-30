package com.example.ferenckovacsx.theringdoctor;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button callButton;
    EditText callerName;
    EditText callerNumber;
    AutoCompleteTextView callRingtone;
    EditText callDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callButton = (Button) findViewById(R.id.call_button);
        callerName = (EditText) findViewById(R.id.inputlayout_name);
        callerNumber = (EditText) findViewById(R.id.inputlayout_number);
        callRingtone = (AutoCompleteTextView) findViewById(R.id.inputlayout_ringtone);
        //callDelay = (TextInputLayout) findViewById(R.id.inputlayout_delay);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listRingtones());

        callRingtone.setAdapter(adapter);
        callRingtone.setKeyListener(null);
        callRingtone.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                ((AutoCompleteTextView) v).showDropDown();
                InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });


        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri currentRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
                Ringtone currentRingtone = RingtoneManager.getRingtone(getApplicationContext(), currentRingtoneUri);
                currentRingtone.play();
            }
        });

        listRingtones();


    }

    public ArrayList<String> listRingtones() {

        ArrayList<String> listOfRingtones = new ArrayList<>();
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        while (cursor.moveToNext()) {
            String ringtoneTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            Uri ringtoneURI = manager.getRingtoneUri(cursor.getPosition());
            listOfRingtones.add(ringtoneTitle);
            Log.i("ringtoneURI" , "" + ringtoneURI);
            Log.i("ringtoneTitle" , "" + ringtoneTitle);
        }

        return listOfRingtones;
    }


}

