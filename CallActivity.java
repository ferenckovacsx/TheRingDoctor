package com.example.ferenckovacsx.theringdoctor;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class CallActivity extends AppCompatActivity {

    TextView callerNameTextView;
    TextView callerNumberTextView;
    ImageView callerImageView;

    Button rejectButton;
    Button answerButton;

    String callerImageFilePath;
    String callerNameString;
    String callerNumberString;
    String ringtoneUriString;
    Boolean vibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_call);

        callerImageView = findViewById(R.id.call_interface_img);
        callerNameTextView = findViewById(R.id.nameTextView);
        callerNumberTextView = findViewById(R.id.numberTextView);

        rejectButton = findViewById(R.id.button_reject_call);
        answerButton = findViewById(R.id.button_answer_call);

        callerImageFilePath = getIntent().getStringExtra("callerImageFilePath");
        callerNameString = getIntent().getStringExtra("callerName");
        callerNumberString = getIntent().getStringExtra("callerNumber");
        ringtoneUriString = getIntent().getStringExtra("callerRingtone");
        vibrate = getIntent().getBooleanExtra("vibrate", false);

        Log.i("callActivity", "imageFilePath: " + callerImageFilePath);

        File croppedImageFile = new File(callerImageFilePath);
        Picasso.with(this).load(croppedImageFile).transform(new CircleTransform()).into(callerImageView);
        callerNameTextView.setText(callerNameString);
        callerNumberTextView.setText(callerNumberString);

        Uri ringtoneUri = Uri.parse(ringtoneUriString);
        final Ringtone currentRingtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
        currentRingtone.play();

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        if (vibrate) {
            long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
            vibrator.vibrate(pattern, -1);

        }

        rejectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                vibrator.cancel();
                currentRingtone.stop();
                finish();

            }
        });
    }
}

