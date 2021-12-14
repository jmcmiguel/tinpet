package com.example.tinpet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.tinpet.R;

public class SplashscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        if(ContextCompat.checkSelfPermission(SplashscreenActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(SplashscreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
           ContextCompat.checkSelfPermission(SplashscreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SplashscreenActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);
                finish();

            }, 2000);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(this, StartActivity.class);
                        startActivity(intent);
                        finish();

                    }, 2000);

                }  else {

                    Toast.makeText(SplashscreenActivity.this, "Need location permission to run", Toast.LENGTH_SHORT).show();

                    System.exit(0);

                }
                return;
        }

    }
}
