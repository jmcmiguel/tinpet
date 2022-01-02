package com.example.tinpet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tinpet.R;

public class Terms extends AppCompatActivity {

    Button back, accept;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        mContext = this;

        back = findViewById(R.id.btn_terms_back);
        accept = findViewById(R.id.btn_accept);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, StartActivity.class);
            startActivity(intent);
            finish();
        });

        accept.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PetRegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
