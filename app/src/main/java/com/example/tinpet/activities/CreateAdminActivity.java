package com.example.tinpet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tinpet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;

import java.util.HashMap;
import java.util.Map;

public class CreateAdminActivity extends AppCompatActivity {

    Button back, create;
    TextInputEditText name, email, password;
    Spinner adminType;
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    FirebaseAuth fAuth;
    private FirebaseFunctions mFunctions;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_admin);

        fAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();

        back = findViewById(R.id.btn_create_admin_back);
        create = findViewById(R.id.btn_create_admin_create);
        name = findViewById(R.id.txt_super_admin_create_name);
        email = findViewById(R.id.txt_super_admin_create_email);
        password = findViewById(R.id.txt_super_admin_create_password);
        adminType = findViewById(R.id.txt_super_admin_create_name_type);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(this,SuperAdminActivity.class);
            startActivity(intent);
            finish();
            return;
        });

        create.setOnClickListener(view -> {

            String finalEmail = email.getText().toString();
            String finalName = name.getText().toString();
            String finalPassword = password.getText().toString();
            String finalAdminType = adminType.getSelectedItem().toString().equals("Admin 1") ? "Admin1" : "Admin2";

            Map<String, Object> data = new HashMap<>();
            data.put("email", finalEmail);
            data.put("password", finalPassword);
            data.put("name", finalName);
            data.put("adminType", finalAdminType);

            createAdminUser(data)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                                Toast.makeText(CreateAdminActivity.this, details.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        Toast.makeText(CreateAdminActivity.this, "Created an admin", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateAdminActivity.this, SuperAdminActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    });

        });
    }

    private Task<String> createAdminUser(Map<String, Object> data) {
        return mFunctions
                .getHttpsCallable("createAdminUser")
                .call(data)
                .continueWith(task -> {
                    String result = (String) task.getResult().getData();
                    return result;
                });
    }
}
