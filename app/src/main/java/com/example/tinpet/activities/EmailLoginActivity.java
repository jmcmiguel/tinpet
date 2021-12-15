package com.example.tinpet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tinpet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmailLoginActivity extends AppCompatActivity {

    Button btnLogin, btnBack;
    TextInputEditText txtEmailAddress, txtPassword;
    Context mContext;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    TextView txtForgotPassword;
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        mContext = this;
        btnLogin = findViewById(R.id.btn_email_login);
        txtEmailAddress = findViewById(R.id.txt_email_login);
        txtPassword = findViewById(R.id.txt_password_login);
        btnBack = findViewById(R.id.btn_email_login_back);
        txtForgotPassword = findViewById(R.id.txt_email_forgot_password);

        // Forgot Password onClick Listener
        txtForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, EmailForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        // Back button onClick listener
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, StartActivity.class);
            startActivity(intent);
            finish();
        });

        // Check if a user is logged in
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference usersRef = dbRef.child("Users");

            if(user != null){
                if(user.getEmail().equals("superadmin@gmail.com")){ // Super admin account
                    Intent intent = new Intent(mContext, SuperAdminActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }else{

                    usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Log.d("users", snapshot.toString());

                            if(snapshot.exists()){
                                if(snapshot.hasChild("approved")){
                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }else if(snapshot.hasChild("adminType")){
                                    if(snapshot.child("adminType").getValue().toString().equals("Admin1")){
                                        Intent intent = new Intent(mContext, Admin1Activity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }else if(snapshot.child("adminType").getValue().toString().equals("Admin2")){
                                        Intent intent = new Intent(mContext, Admin2Activity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                }else{
                                    firebaseAuth.signOut();

                                    String errorMessage = "Cannot Sign in.";

                                    if(!user.isEmailVerified())
                                        errorMessage += " Please verify your email.";
                                    if(!snapshot.hasChild("approved"))
                                        errorMessage += " Please wait for admin to approve your account";

                                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        };

        // Register Button onClick Listener
        btnLogin.setOnClickListener(view -> {
            final String email = txtEmailAddress.getText().toString();
            final String password = txtPassword.getText().toString();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(EmailLoginActivity.this, task -> {
                if(!task.isSuccessful()){
                    Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            });
        });


    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.signOut();
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
