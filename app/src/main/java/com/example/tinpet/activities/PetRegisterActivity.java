package com.example.tinpet.activities;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tinpet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PetRegisterActivity extends AppCompatActivity {

    Context mContext;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    TextInputEditText txtPetName;
    EditText txtPetBday;
    Spinner spnrPetBreed, spnrPetBreed2, spnrPetBreed3, spnrPetBreed4, spnrPetBreed5, spnrPetSize;
    String petGender;
    Button btnPetNext, btnBack;
    Calendar petBdayCalendar;
    TextInputLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_register);

        mContext = this;

        mLayout = findViewById(R.id.breed_layout);
        txtPetName = findViewById(R.id.txt_pet_name);
        txtPetBday = findViewById(R.id.pet_bday);
        spnrPetBreed = findViewById(R.id.spnr_breed);
        spnrPetBreed2 = findViewById(R.id.spnr_breed2);
        spnrPetBreed3 = findViewById(R.id.spnr_breed3);
        spnrPetBreed4 = findViewById(R.id.spnr_breed4);
        spnrPetBreed5 = findViewById(R.id.spnr_breed5);
        btnPetNext = findViewById(R.id.btn_pet_next);
        btnBack = findViewById(R.id.btn_pet_register_back);
        spnrPetSize = findViewById(R.id.spnr_breed_size);



        spnrPetSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String breedSize = adapterView.getItemAtPosition(i).toString();
                String[] arraySpinner = mContext.getResources().getStringArray(R.array.small_breeds_array);

                if(breedSize.equals("Small Breeds")){
                    arraySpinner = mContext.getResources().getStringArray(R.array.small_breeds_array);
                    spnrPetBreed2.setVisibility(View.GONE);
                    spnrPetBreed3.setVisibility(View.GONE);
                    spnrPetBreed4.setVisibility(View.GONE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Medium Breeds")){
                    arraySpinner = mContext.getResources().getStringArray(R.array.medium_breeds_array);
                    spnrPetBreed2.setVisibility(View.GONE);
                    spnrPetBreed3.setVisibility(View.GONE);
                    spnrPetBreed4.setVisibility(View.GONE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Large Breeds")){
                    arraySpinner = mContext.getResources().getStringArray(R.array.large_breeds_array);
                    spnrPetBreed2.setVisibility(View.GONE);
                    spnrPetBreed3.setVisibility(View.GONE);
                    spnrPetBreed4.setVisibility(View.GONE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Mixed Breeds (2 Breeds)")){
                    arraySpinner = mContext.getResources().getStringArray(R.array.all_breeds_array);
                    spnrPetBreed2.setVisibility(View.VISIBLE);
                    spnrPetBreed3.setVisibility(View.GONE);
                    spnrPetBreed4.setVisibility(View.GONE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Mixed Breeds (3 Breeds)")){
                    arraySpinner = mContext.getResources().getStringArray(R.array.all_breeds_array);
                    spnrPetBreed2.setVisibility(View.VISIBLE);
                    spnrPetBreed3.setVisibility(View.VISIBLE);
                    spnrPetBreed4.setVisibility(View.GONE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Mixed Breeds (4 Breeds)")){
                    arraySpinner = mContext.getResources().getStringArray(R.array.all_breeds_array);
                    spnrPetBreed2.setVisibility(View.VISIBLE);
                    spnrPetBreed3.setVisibility(View.VISIBLE);
                    spnrPetBreed4.setVisibility(View.VISIBLE);
                    spnrPetBreed5.setVisibility(View.GONE);
                }else if(breedSize.equals("Mixed Breeds (5 Breeds)")){
                    arraySpinner = mContext.getResources().getStringArray(R.array.all_breeds_array);
                    spnrPetBreed2.setVisibility(View.VISIBLE);
                    spnrPetBreed3.setVisibility(View.VISIBLE);
                    spnrPetBreed4.setVisibility(View.VISIBLE);
                    spnrPetBreed5.setVisibility(View.VISIBLE);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PetRegisterActivity.this,
                        android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnrPetBreed.setAdapter(adapter);

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

        // back button onClick listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, Terms.class);
                startActivity(intent);
                finish();
            }
        });

        // Birthdate onClickListener
        // Show Calendar Modal onClick
        txtPetBday.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year1, int month1, int day1) {
                    month1 = month1 + 1;
                    String date = month1 + "/" + day1 + "/" + year1;
                    txtPetBday.setText(date);
                }
            }, year, month, day);

            datePickerDialog.show();
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if(user != null){
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        };

        // Next Button onClick Listener
        btnPetNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PetRegisterActivity.this, EmailRegisterActivity.class);
                intent.putExtra("pet_name", txtPetName.getText().toString());
                intent.putExtra("pet_breed", spnrPetBreed.getSelectedItem().toString());
                intent.putExtra("breed_size", spnrPetSize.getSelectedItem().toString());

                if(spnrPetSize.getSelectedItem().toString().equals("Mixed Breeds (2 Breeds)")){
                    intent.putExtra("pet_breed2", spnrPetBreed2.getSelectedItem().toString());
                }else if(spnrPetSize.getSelectedItem().toString().equals("Mixed Breeds (3 Breeds)")){
                    intent.putExtra("pet_breed2", spnrPetBreed2.getSelectedItem().toString());
                    intent.putExtra("pet_breed3", spnrPetBreed3.getSelectedItem().toString());
                }else if(spnrPetSize.getSelectedItem().toString().equals("Mixed Breeds (4 Breeds)")){
                    intent.putExtra("pet_breed2", spnrPetBreed2.getSelectedItem().toString());
                    intent.putExtra("pet_breed3", spnrPetBreed3.getSelectedItem().toString());
                    intent.putExtra("pet_breed4", spnrPetBreed4.getSelectedItem().toString());
                }else if(spnrPetSize.getSelectedItem().toString().equals("Mixed Breeds (5 Breeds)")){
                    intent.putExtra("pet_breed2", spnrPetBreed2.getSelectedItem().toString());
                    intent.putExtra("pet_breed3", spnrPetBreed3.getSelectedItem().toString());
                    intent.putExtra("pet_breed4", spnrPetBreed4.getSelectedItem().toString());
                    intent.putExtra("pet_breed5", spnrPetBreed5.getSelectedItem().toString());
                }

                intent.putExtra("pet_bday", txtPetBday.getText().toString());
                intent.putExtra("pet_gender", petGender);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
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

}
