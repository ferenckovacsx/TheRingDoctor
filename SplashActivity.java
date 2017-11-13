package com.example.ferenckovacsx.theringdoctor;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#55B8D8"));
        }

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Intent startMainActivity = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(startMainActivity);
            }
        }, 3000);
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);


        ImageView doctorImage = findViewById(R.id.ring_animation_imageview);
        doctorImage.setAdjustViewBounds(true);
        doctorImage.setScaleType(ImageView.ScaleType.CENTER);
        doctorImage.setImageDrawable(getResources().getDrawable(R.drawable.ring_animation));

        AnimationDrawable ringAnimation = (AnimationDrawable) doctorImage.getDrawable();
        ringAnimation.start();
    }
}

