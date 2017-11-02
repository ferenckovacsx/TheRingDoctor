package com.example.ferenckovacsx.theringdoctor;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CallActivity extends AppCompatActivity {

    TextView callerNameTextView;
    TextView callerNumberTextView;

    Button rejectButton;
    Button answerButton;

    String callerNameString;
    String callerNumberString;
    String ringtoneUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        callerNameTextView = (TextView) findViewById(R.id.nameTextView);
        callerNumberTextView = (TextView) findViewById(R.id.numberTextView);

        rejectButton = (Button) findViewById(R.id.button_reject_call);
        answerButton = (Button) findViewById(R.id.button_answer_call);

        callerNameString = getIntent().getStringExtra("callerName");
        callerNumberString = getIntent().getStringExtra("callerNumber");
        ringtoneUriString = getIntent().getStringExtra("callerRingtone");

        Log.i("callActivity", "ringtoneUri: " + ringtoneUriString);

        callerNameTextView.setText(callerNameString);
        callerNumberTextView.setText(callerNumberString);

        Uri ringtoneUri = Uri.parse(ringtoneUriString);
        final Ringtone currentRingtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
        currentRingtone.play();

        rejectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                currentRingtone.stop();
                finish();

            }
        });
    }
}

