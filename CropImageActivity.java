package com.example.ferenckovacsx.theringdoctor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lyft.android.scissors.CropView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import static android.graphics.Bitmap.CompressFormat.JPEG;

public class CropImageActivity extends AppCompatActivity {

    CropView cropView;
    FloatingActionButton cropButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        cropButton = findViewById(R.id.cropButton);
        cropView = findViewById(R.id.cropView);

        String imageUriString = getIntent().getStringExtra("imageUri");
        Uri imageUri = Uri.parse(imageUriString);

        Picasso.with(this)
                .load(imageUri)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(cropView);

        cropButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(CropImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(CropImageActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                }

                if (ContextCompat.checkSelfPermission(CropImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(CropImageActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                }

                String fileDir = Environment.DIRECTORY_PICTURES + File.separator + "ringDoctor" + File.separator;

                final File croppedFile = new File(getFilesDir(), "cropped.jpg");

                cropView.extensions()
                        .crop()
                        .quality(100)
                        .format(JPEG)
                        .into(croppedFile);

                Intent cropImageIntent = new Intent(CropImageActivity.this, MainActivity.class);
                cropImageIntent.putExtra("croppedImageUri", croppedFile.getPath());
                startActivity(cropImageIntent);

            }
        });
    }
}
