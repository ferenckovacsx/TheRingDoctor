package com.example.ferenckovacsx.theringdoctor;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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


        ImageView rocketImage = (ImageView) findViewById(R.id.ring_animation_imageview);
        rocketImage.setAdjustViewBounds(true);
        rocketImage.setScaleType(ImageView.ScaleType.CENTER);
        rocketImage.setImageDrawable(getResources().getDrawable(R.drawable.ring_animation));

        AnimationDrawable rocketAnimation = (AnimationDrawable) rocketImage.getDrawable();
        rocketAnimation.start();
    }
}

