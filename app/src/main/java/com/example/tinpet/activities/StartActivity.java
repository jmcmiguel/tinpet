package com.example.tinpet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tinpet.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity {

    Button btnEmail;
    TextView txtRegister;
    Context mContext;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListner = firebaseAuth -> {

            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference usersRef = dbRef.child("Users");

            if(user != null){
                if(user.getEmail().equals("superadmin@test.com")){ // Super admin account
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
                                if(user.isEmailVerified() && snapshot.hasChild("approved")){
                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;
                                }else if(user.isEmailVerified() && snapshot.hasChild("adminType")){
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

        mContext = this;

        btnEmail = findViewById(R.id.btn_email_connect);
        txtRegister = findViewById(R.id.txt_signup);

        btnEmail.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, EmailLoginActivity.class);
            startActivity(intent);
            finish();
        });

        txtRegister.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, Terms.class);
            startActivity(intent);
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListner);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListner);
    }

    /*public void setSpannableStringText() {

        SpannableString ss = new SpannableString(getResources().getString(R.string.start_text_advice));
        ClickableSpan clickableSpanTerms = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                //startActivity(new Intent(StartActivity.this, TermsActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan clickableSpanPolicy = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                //startActivity(new Intent(StartActivity.this, PrivacyPolicyActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpanTerms, 51, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpanPolicy, 68, 82, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 51, 63, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 68, 82, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = findViewById(R.id.text_policy_terms);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);

    }*/
}
