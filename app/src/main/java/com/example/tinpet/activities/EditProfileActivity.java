package com.example.tinpet.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tinpet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    Button back, update;
    TextInputEditText fname, lname, email, password, pet_name;
    EditText bday, petBday;
    Spinner gender, breed, spnrPetSize, spnrPetBreed2, spnrPetBreed3, spnrPetBreed4, spnrPetBreed5;
    RadioButton petMale, petFemale;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid();
    AuthCredential credential;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    Calendar petBdayCalendar;
    String petGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        back = findViewById(R.id.btn_profile_update_back);
        fname = findViewById(R.id.profile_edit_fname);
        lname = findViewById(R.id.profile_edit_lname);
        email = findViewById(R.id.profile_edit_email);
        password = findViewById(R.id.profile_edit_password);
        pet_name = findViewById(R.id.profile_edit_petName);
        bday = findViewById(R.id.profile_edit_bday);
        gender = findViewById(R.id.profile_edit_gender);
        breed = findViewById(R.id.spnr_breed);
        spnrPetBreed2 = findViewById(R.id.spnr_breed2);
        spnrPetBreed3 = findViewById(R.id.spnr_breed3);
        spnrPetBreed4 = findViewById(R.id.spnr_breed4);
        spnrPetBreed5 = findViewById(R.id.spnr_breed5);
        petBday = findViewById(R.id.pet_bday);
        petMale = findViewById(R.id.pet_gender_male);
        petFemale = findViewById(R.id.pet_gender_female);
        update = findViewById(R.id.profile_edit_update);
        spnrPetSize = findViewById(R.id.edit_spnr_breed_size);

        email.setEnabled(false);

        spnrPetSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String breedSize = adapterView.getItemAtPosition(i).toString();
                String[] arraySpinner = EditProfileActivity.this.getResources().getStringArray(R.array.small_breeds_array);

                if(breedSize.equals("Small Breeds")){
                    arraySpinner = EditProfileActivity.this.getResources().getStringArray(R.array.small_breeds_array);
                    spnrPetBreed2.setVisibility(View.GONE);
                    spnrPetBreed3.setVisibility(View.GONE);
                    spnrPetBreed4.setVisibility(View.GONE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Medium Breeds")){
                    arraySpinner = EditProfileActivity.this.getResources().getStringArray(R.array.medium_breeds_array);
                    spnrPetBreed2.setVisibility(View.GONE);
                    spnrPetBreed3.setVisibility(View.GONE);
                    spnrPetBreed4.setVisibility(View.GONE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Large Breeds")){
                    arraySpinner = EditProfileActivity.this.getResources().getStringArray(R.array.large_breeds_array);
                    spnrPetBreed2.setVisibility(View.GONE);
                    spnrPetBreed3.setVisibility(View.GONE);
                    spnrPetBreed4.setVisibility(View.GONE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Mixed Breeds (2 Breeds)")){
                    arraySpinner = EditProfileActivity.this.getResources().getStringArray(R.array.all_breeds_array);
                    spnrPetBreed2.setVisibility(View.VISIBLE);
                    spnrPetBreed3.setVisibility(View.GONE);
                    spnrPetBreed4.setVisibility(View.GONE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Mixed Breeds (3 Breeds)")){
                    arraySpinner = EditProfileActivity.this.getResources().getStringArray(R.array.all_breeds_array);
                    spnrPetBreed2.setVisibility(View.VISIBLE);
                    spnrPetBreed3.setVisibility(View.VISIBLE);
                    spnrPetBreed4.setVisibility(View.GONE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Mixed Breeds (4 Breeds)")){
                    arraySpinner = EditProfileActivity.this.getResources().getStringArray(R.array.all_breeds_array);
                    spnrPetBreed2.setVisibility(View.VISIBLE);
                    spnrPetBreed3.setVisibility(View.VISIBLE);
                    spnrPetBreed4.setVisibility(View.VISIBLE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Mixed Breeds (5 Breeds)")){
                    arraySpinner = EditProfileActivity.this.getResources().getStringArray(R.array.all_breeds_array);
                    spnrPetBreed2.setVisibility(View.VISIBLE);
                    spnrPetBreed3.setVisibility(View.VISIBLE);
                    spnrPetBreed4.setVisibility(View.VISIBLE);
                    spnrPetBreed5.setVisibility(View.VISIBLE);
                }


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditProfileActivity.this,
                        android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                breed.setAdapter(adapter);

                if(breedSize.equals("Mixed Breeds (2 Breeds)")){
                    spnrPetBreed2.setAdapter(adapter);
                }else if(breedSize.equals("Mixed Breeds (3 Breeds)")){
                    spnrPetBreed2.setAdapter(adapter);
                    spnrPetBreed3.setAdapter(adapter);
                }else if(breedSize.equals("Mixed Breeds (4 Breeds)")){
                    spnrPetBreed2.setAdapter(adapter);
                    spnrPetBreed3.setAdapter(adapter);
                    spnrPetBreed4.setAdapter(adapter);
                }else if(breedSize.equals("Mixed Breeds (5 Breeds)")){
                    spnrPetBreed2.setAdapter(adapter);
                    spnrPetBreed3.setAdapter(adapter);
                    spnrPetBreed4.setAdapter(adapter);
                    spnrPetBreed5.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        petBdayCalendar = Calendar.getInstance();

        final int year = petBdayCalendar.get(Calendar.YEAR);
        final int month = petBdayCalendar.get(Calendar.MONTH);
        final int day = petBdayCalendar.get(Calendar.DAY_OF_MONTH);

        // Birthdate onClickListener
        // Show Calendar Modal onClick
        petBday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditProfileActivity.this, (datePicker, year1, month1, day1) -> {
                            month1 = month1 + 1;
                            String date = month1 + "/" + day1 + "/" + year1;
                    petBday.setText(date);
                        }, year, month, day);

                datePickerDialog.show();
            }
        });


        update.setOnClickListener(view -> {
            String userNode = "Users/" + uid + "/";
            String petNode = "Pets/" + uid + "/";

            Map userInfo = new HashMap<>();
            userInfo.put(userNode + "fname", fname.getText().toString());
            userInfo.put(userNode + "lname", lname.getText().toString());
            userInfo.put(userNode + "email", email.getText().toString());
            userInfo.put(userNode + "gender", gender.getSelectedItem().toString());
            userInfo.put(userNode + "bday", bday.getText().toString());

            if(petGender == null){
                if(petMale.isChecked()){
                    petGender = "Male";
                }else if(petFemale.isChecked()){
                    petGender = "Female";
                }
            }

            userInfo.put(petNode + "pet_gender", petGender);
            userInfo.put(petNode + "pet_name", pet_name.getText().toString());
            userInfo.put(petNode + "pet_breed", breed.getSelectedItem().toString());
            userInfo.put(petNode + "pet_bday", petBday.getText().toString());
            userInfo.put(petNode + "pet_size", spnrPetSize.getSelectedItem().toString());

            if(spnrPetBreed2.getSelectedItem() != null)
                userInfo.put(petNode + "pet_breed2", spnrPetBreed2.getSelectedItem().toString());

            if(spnrPetBreed3.getSelectedItem() != null)
                userInfo.put(petNode + "pet_breed3", spnrPetBreed3.getSelectedItem().toString());

            if(spnrPetBreed4.getSelectedItem() != null)
                userInfo.put(petNode + "pet_breed4", spnrPetBreed4.getSelectedItem().toString());

            if(spnrPetBreed5.getSelectedItem() != null)
                userInfo.put(petNode + "pet_breed5", spnrPetBreed5.getSelectedItem().toString());

            databaseReference.updateChildren(userInfo).addOnCompleteListener(task1 -> {
                if(!task1.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
                    onBackPressed();
                    return;
                }
            });
        });

        back.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    public void genderRbClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.pet_gender_male:
                if (checked)
                    petGender = "Male";
                break;
            case R.id.pet_gender_female:
                if (checked)
                    petGender = "Female";
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.child("Users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    fname.setText(snapshot.child("fname").getValue().toString());
                    lname.setText(snapshot.child("lname").getValue().toString());
                    email.setText(snapshot.child("email").getValue().toString());
                    bday.setText(snapshot.child("bday").getValue().toString());

                    for (int i=0;i<gender.getCount();i++){
                        if (gender.getItemAtPosition(i).toString().equalsIgnoreCase(snapshot.child("gender").getValue().toString())){
                            gender.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // get pet info
        databaseReference.child("Pets/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    pet_name.setText(snapshot.child("pet_name").getValue().toString());
                    petBday.setText(snapshot.child("pet_bday").getValue().toString());

                    if(snapshot.child("pet_gender").getValue().toString().equals("Male")){
                        petMale.toggle();
                    }else{
                        petFemale.toggle();
                    }

                    if(snapshot.child("pet_size").getValue().toString().equals("Small Breeds")){
                        spnrPetSize.setSelection(0);
                    }else if(snapshot.child("pet_size").getValue().toString().equals("Medium Breeds")){
                        spnrPetSize.setSelection(1);
                    }else if(snapshot.child("pet_size").getValue().toString().equals("Large Breeds")){
                        spnrPetSize.setSelection(2);
                    }else if(snapshot.child("pet_size").getValue().toString().equals("Mixed Breeds (2 Breeds)")){
                        spnrPetSize.setSelection(3);
                    }else if(snapshot.child("pet_size").getValue().toString().equals("Mixed Breeds (3 Breeds)")){
                        spnrPetSize.setSelection(4);
                    }else if(snapshot.child("pet_size").getValue().toString().equals("Mixed Breeds (4 Breeds)")){
                        spnrPetSize.setSelection(5);
                    }else if(snapshot.child("pet_size").getValue().toString().equals("Mixed Breeds (5 Breeds)")){
                        spnrPetSize.setSelection(6);
                    }

                    for (int i=0;i<breed.getCount();i++){
                        if (breed.getItemAtPosition(i).toString().equalsIgnoreCase(snapshot.child("pet_breed").getValue().toString())){
                            breed.setSelection(i);
                            break;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
