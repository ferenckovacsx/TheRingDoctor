package com.example.ferenckovacsx.theringdoctor;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerNameTag;
import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerNumberTag;
import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerRingtoneTag;
import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerRingtoneUriTag;
import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerVibrateTag;
import static com.example.ferenckovacsx.theringdoctor.MainActivity.callerVoiceTag;

public class CallActivity extends AppCompatActivity {

    TextView callerNameTextView;
    TextView callerNumberTextView;
    ImageView callerImageView;

    TextView rejectButton;
    TextView answerButton;
    TextView endCallButton;

    String callerImageFilePath;
    String callerNameString;
    String callerNumberString;
    String callerVoiceString;
    String ringtoneUriString;
    Boolean vibrate;

    Ringtone currentRingtone;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#062136"));
        }

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
        endCallButton = findViewById(R.id.button_end_call);
        endCallButton.setVisibility(View.INVISIBLE);

        callerImageFilePath = getIntent().getStringExtra("callerImageFilePath");
        callerNameString = getIntent().getStringExtra(callerNameTag);
        callerNumberString = getIntent().getStringExtra(callerNumberTag);
        callerVoiceString = getIntent().getStringExtra(callerVoiceTag);
        ringtoneUriString = getIntent().getStringExtra(callerRingtoneUriTag);
        vibrate = getIntent().getBooleanExtra(callerVibrateTag, false);

        Log.i("callActivity", "imageFilePath: " + callerImageFilePath);
        Log.i("callActivity", "callerName: " + callerNameString);
        Log.i("callActivity", "callerVoice: " + callerVoiceString);

        File croppedImageFile = new File(callerImageFilePath);

        if (callerImageFilePath.equals("")) {
            Picasso.with(this).load(R.drawable.anonymus_profile).transform(new CircleTransform()).into(callerImageView);

        } else {
            Picasso.with(this).load(croppedImageFile).transform(new CircleTransform()).into(callerImageView);

        }

        if (callerNameString.equals("")) {
            callerNameTextView.setText("");
        } else {
            callerNameTextView.setText(callerNameString);
        }

        if (callerNumberString.equals("")) {
            callerNumberTextView.setText("Unknown number");
        } else {
            callerNumberTextView.setText(callerNumberString);
        }


        Uri ringtoneUri = Uri.parse(ringtoneUriString);
        currentRingtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
        currentRingtone.play();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        if (vibrate) {
            long[] pattern = {2000, 500, 2000, 500};
            vibrator.vibrate(pattern, 1);

        }

        mediaPlayer = new MediaPlayer();

        rejectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                mediaPlayer.stop();
                vibrator.cancel();
                currentRingtone.stop();
                finish();

            }
        });

        endCallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                mediaPlayer.stop();
                vibrator.cancel();
                currentRingtone.stop();
                finish();

            }
        });

        answerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                vibrator.cancel();
                currentRingtone.stop();
                endCallButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.INVISIBLE);
                answerButton.setVisibility(View.INVISIBLE);

                if (!callerVoiceString.equals("None") || !callerVoiceString.equals("")) {
                    try {
                        if (!mediaPlayer.isPlaying()) {
                            AssetFileDescriptor afd = getAssets().openFd(callerVoiceString + ".mp3");
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentRingtone.stop();
        mediaPlayer.stop();
        vibrator.cancel();
    }
}

