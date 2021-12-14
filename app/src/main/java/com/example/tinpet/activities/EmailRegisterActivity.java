package com.example.tinpet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tinpet.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EmailRegisterActivity extends AppCompatActivity {

    Spinner spnrGender;
    Button btnRegister, btnBack, btnMedcert, btnValidID;
    TextInputEditText txtFirstName, txtLastName, txtEmailAddress, txtPassword;
    EditText txtBday;
    Calendar bdayCalendar;
    Context mContext;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser authCurrentUser = firebaseAuth.getCurrentUser();
    Uri valid_id_uri;
    Uri pet_med_cert;
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

    ImageView medcertImg;
    ImageView idImg;

    String validID = null;
    String medCert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_register);

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(EmailRegisterActivity.this).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);


        mContext = this;

        spnrGender = findViewById(R.id.spnr_register_gender);
        txtFirstName = findViewById(R.id.txt_first_name);
        txtLastName = findViewById(R.id.txt_last_name);
        txtBday = findViewById(R.id.register_bday);
        txtEmailAddress = findViewById(R.id.txt_register_email);
        txtBday = findViewById(R.id.register_bday);
        btnRegister = findViewById(R.id.btn_email_register);
        txtPassword = findViewById(R.id.txt_register_password);
        btnBack = findViewById(R.id.btn_email_register_back);
        btnMedcert = findViewById(R.id.btn_medcert);
        btnValidID = findViewById(R.id.btn_valid_id);
        medcertImg = findViewById(R.id.medcert_img);
        idImg = findViewById(R.id.valid_id_img);

        bdayCalendar = Calendar.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final int year = bdayCalendar.get(Calendar.YEAR);
        final int month = bdayCalendar.get(Calendar.MONTH);
        final int day = bdayCalendar.get(Calendar.DAY_OF_MONTH);

        // Select Image onClick
        btnValidID.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        // Select Image onClick
        btnMedcert.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 2);
        });

        // Back button onClick listener
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PetRegisterActivity.class);
            startActivity(intent);
            finish();
        });

        // Birthdate onClickListener
        // Show Calendar Modal onClick
        txtBday.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year1, int month1, int day1) {
                    month1 = month1 + 1;
                    String date = month1 + "/" + day1 + "/" + year1;
                    txtBday.setText(date);
                }
            }, year, month, day);

            datePickerDialog.show();
        });


        // Register Button onClick Listener
        btnRegister.setOnClickListener(view -> {

            final String email = txtEmailAddress.getText().toString();
            final String password = txtPassword.getText().toString();
            final String fname = txtFirstName.getText().toString();
            final String lname = txtLastName.getText().toString();
            final String gender = spnrGender.getSelectedItem().toString();
            final String bday = txtBday.getText().toString();

            Bundle extras = getIntent().getExtras();
            final String petName = extras.getString("pet_name");
            final String petBreed = extras.getString("pet_breed");
            final String petBreed2 = extras.getString("pet_breed2");
            final String petBreed3 = extras.getString("pet_breed3");
            final String petBreed4 = extras.getString("pet_breed4");
            final String petBreed5 = extras.getString("pet_breed5");
            final String petBday = extras.getString("pet_bday");
            final String petGender = extras.getString("pet_gender");
            final String petSize = extras.getString("breed_size");

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(EmailRegisterActivity.this, task -> {
                if(!task.isSuccessful()){
                    Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
                    String userNode = "Users/" + userId + "/";
                    String petNode = "Pets/" + userId + "/";

                    Map userInfo = new HashMap<>();
                    userInfo.put(userNode + "fname", fname);
                    userInfo.put(userNode + "lname", lname);
                    userInfo.put(userNode + "email", email);
                    userInfo.put(userNode + "gender", gender);
                    userInfo.put(userNode + "bday", bday);
                    userInfo.put(petNode + "pet_gender", petGender);
                    userInfo.put(petNode + "pet_name", petName);
                    userInfo.put(petNode + "pet_breed", petBreed);
                    userInfo.put(petNode + "pet_bday", petBday);
                    userInfo.put(petNode + "pet_size", petSize);

                    if(petSize.equals("Mixed Breeds (2 Breeds)")){
                        userInfo.put(petNode + "pet_breed2", petBreed2);
                    }else if(petSize.equals("Mixed Breeds (3 Breeds)")){
                        userInfo.put(petNode + "pet_breed2", petBreed2);
                        userInfo.put(petNode + "pet_breed3", petBreed3);
                    }else if(petSize.equals("Mixed Breeds (4 Breeds)")){
                        userInfo.put(petNode + "pet_breed2", petBreed2);
                        userInfo.put(petNode + "pet_breed3", petBreed3);
                        userInfo.put(petNode + "pet_breed4", petBreed4);
                    }else if(petSize.equals("Mixed Breeds (5 Breeds)")){
                        userInfo.put(petNode + "pet_breed2", petBreed2);
                        userInfo.put(petNode + "pet_breed3", petBreed3);
                        userInfo.put(petNode + "pet_breed4", petBreed4);
                        userInfo.put(petNode + "pet_breed5", petBreed5);
                    }

                    userInfo.put(petNode + "age_pref", 20);
                    userInfo.put(petNode + "maximum_distance", 60);
                    userInfo.put(petNode + "size_pref", "Small Breeds");
                    userInfo.put(userNode + "validID", validID);
                    userInfo.put(userNode + "medCert", medCert);

                    databaseReference.updateChildren(userInfo).addOnCompleteListener(task1 -> {
                        if(!task1.isSuccessful()){
                            Toast.makeText(mContext, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            Toast.makeText(mContext, "Please verify your account and wait for your account to be verified. Thank you!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(mContext, EmailLoginActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    });
                }
            });
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            valid_id_uri = imageUri;

            Glide.with(EmailRegisterActivity.this)
                    .load(imageUri)
                    .into(idImg);

            if(valid_id_uri != null){
                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("validID").child(UUID.randomUUID().toString());
                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(EmailRegisterActivity.this.getContentResolver(), valid_id_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] byteArray = baos.toByteArray();
                UploadTask uploadTask = filepath.putBytes(byteArray);
                uploadTask.addOnFailureListener(e -> Log.e("image upload", e.getMessage()));

                uploadTask.addOnSuccessListener(taskSnapshot -> {

                    filepath.getDownloadUrl().addOnSuccessListener(uri -> {

                        validID = uri.toString();

                    });

                    return;
                });
            }

            Log.d("finished upload", String.valueOf(valid_id_uri));
        }else if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            pet_med_cert = imageUri;

            Glide.with(EmailRegisterActivity.this)
                    .load(imageUri)
                    .into(medcertImg);

            if(pet_med_cert != null){
                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("medcert").child(UUID.randomUUID().toString());
                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(EmailRegisterActivity.this.getContentResolver(), pet_med_cert);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] byteArray = baos.toByteArray();
                UploadTask uploadTask = filepath.putBytes(byteArray);
                uploadTask.addOnFailureListener(e -> Log.e("image upload", e.getMessage()));

                uploadTask.addOnSuccessListener(taskSnapshot -> {

                    filepath.getDownloadUrl().addOnSuccessListener(uri -> {

                        medCert = uri.toString();

                    });

                    return;
                });
            }

            Log.d("finished upload", String.valueOf(valid_id_uri));
        }
    }


}