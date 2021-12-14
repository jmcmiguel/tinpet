package com.example.tinpet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tinpet.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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

public class UpdateAdminActivity extends AppCompatActivity {

    Button back, update, delete;
    TextInputEditText name, email;
    Spinner adminType;
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    FirebaseAuth fAuth;
    String extrasuid;
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_admin);

        mFunctions = FirebaseFunctions.getInstance();

        back = findViewById(R.id.btn_update_admin_back);
        update = findViewById(R.id.btn_create_admin_update);
        delete = findViewById(R.id.btn_create_admin_delete);

        name = findViewById(R.id.txt_super_admin_update_name);
        email = findViewById(R.id.txt_super_admin_update_email);
        adminType = findViewById(R.id.txt_super_admin_update_name_type);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(this, SuperAdminActivity.class);
            startActivity(intent);
            finish();
        });

        update.setOnClickListener(view -> {

            if(name.getText().equals("") || email.getText().equals("")){
                Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show();
            }else{
                String adminNode = "Admins/" + extrasuid + "/";
                Map userInfo = new HashMap<>();
                userInfo.put(adminNode + "name", name.getText().toString());
                userInfo.put(adminNode + "adminType", adminType.getSelectedItem().toString());
                //@TODO: Include user password when updating data
                dbRef.updateChildren(userInfo);

                Toast.makeText(this, "Updated Admin", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SuperAdminActivity.class);
                startActivity(intent);
                finish();
            }
            return;
        });

        delete.setOnClickListener(view -> {
            dbRef.child("Admins/" + extrasuid + "/").getRef().removeValue();

            Map<String, Object> data = new HashMap<>();
            data.put("uid", extrasuid);

            deleteAdminUser(data)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                                Toast.makeText(UpdateAdminActivity.this, details.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        Toast.makeText(this, "Deleted Admin", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, SuperAdminActivity.class);
                        startActivity(intent);
                        finish();
                    });


        });

        getData();


    }

    private void getData() {
        Bundle extras = getIntent().getExtras();
        extrasuid = extras.getString("uid");

        dbRef.child("Admins/" + extrasuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    name.setText(snapshot.child("name").getValue().toString());
                    email.setText(snapshot.child("email").getValue().toString());
                    email.setEnabled(false);
                    //@TODO INCLUDE PASSWORD WHEN FETCHING DATA

                    Map<String, Object> data = new HashMap<>();
                    data.put("uid", extrasuid);

                    adminType.setSelection(snapshot.child("adminType").getValue().toString().equals("Admin 1") ? 0 : 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Task<String> deleteAdminUser(Map<String, Object> data) {
        return mFunctions
                .getHttpsCallable("deleteAdminUser")
                .call(data)
                .continueWith(task -> {
                    String result = (String) task.getResult().getData();
                    return result;
                });
    }

}
