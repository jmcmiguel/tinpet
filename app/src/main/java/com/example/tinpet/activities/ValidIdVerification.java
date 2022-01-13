package com.example.tinpet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.example.tinpet.R;

import java.io.File;
import java.io.FileOutputStream;

public class ValidIdVerification extends AppCompatActivity {

    private CameraKitView cameraKitView;
    private Button btn_capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valid_id_verification);

        cameraKitView = findViewById(R.id.camera);
        btn_capture = findViewById(R.id.btn_valid_id_capture);

        // Capture Button onClick Listener
        btn_capture.setOnClickListener(ValidIdPhotoOnClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }
    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }
    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Capture Button OnClick Listener
    private View.OnClickListener ValidIdPhotoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraKitView.captureImage((cameraKitView, capturedImage) -> {

                File savedPhoto = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

                try {
                    FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                    outputStream.write(capturedImage);
                    outputStream.close();

                    Intent intent = new Intent(ValidIdVerification.this, ValidIdView.class);
                    intent.putExtra("validIDFront", savedPhoto.getPath());
                    startActivity(intent);
                    finish();

                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            });
        }
    };
}