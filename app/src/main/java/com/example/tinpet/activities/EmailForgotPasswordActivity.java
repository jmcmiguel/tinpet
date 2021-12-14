package com.example.tinpet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tinpet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class EmailForgotPasswordActivity extends AppCompatActivity {

    Button btnForgotPasswordBack, btnForgotPassword;
    EditText txtEmail;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_forgot_password);

        btnForgotPassword = findViewById(R.id.btn_email_forgot_password);
        btnForgotPasswordBack = findViewById(R.id.btn_email_forgot_password_back);
        txtEmail = findViewById(R.id.txt_forgot_password_email);
        mContext = this;

        // Forgot Password onClick listener
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = txtEmail.getText().toString();

                try{
                    // Send email reset link
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, "Email link sent", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(mContext, "Cannot send email link", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }catch(Exception e){
                    Log.e("forgot-password", e.getMessage());
                }


            }
        });

        // Back button onClick listener
        btnForgotPasswordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EmailLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

}
