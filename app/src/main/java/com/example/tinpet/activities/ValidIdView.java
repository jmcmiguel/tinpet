package com.example.tinpet.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tinpet.R;

import java.io.File;

public class ValidIdView extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.valid_id_view);

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.frontIDContainer);
        Button retake = findViewById(R.id.btn_retake_front_id);
        Button proceedToBack = findViewById(R.id.btn_proceed_back_id);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        ImageView imageView = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        imageView.setLayoutParams(params);

        File imageFile = new File(getIntent().getStringExtra("validIDFront"));
        Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        imageView.setImageBitmap(myBitmap);

        layout.addView(imageView);

        // Retake Button onClick Listener
        retake.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ValidIdVerification.class);
            startActivity(intent);
            finish();
        });

    }
}
