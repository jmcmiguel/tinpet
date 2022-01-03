package com.example.tinpet.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.tinpet.R;
import com.example.tinpet.activities.EditProfileActivity;
import com.example.tinpet.activities.EmailLoginActivity;
import com.example.tinpet.activities.MessageActivity;
import com.example.tinpet.activities.StartActivity;
import com.example.tinpet.adapters.SliderAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {


    View rootLayout;
    ImageView btnSettings, btnChangeDp, btnAccountEdit;
    TextView txtAccountName, txtAccountDetails, txtAccountAddress;
    CircleImageView profilePhoto;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference userRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users").child(userId);
    DatabaseReference petRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Pets").child(userId);
    Map userInfo = new HashMap<>();
    Map petInfo = new HashMap<>();
    private Uri resultUri;
    DialogPlus dialog;
    String breedSizePref;

    public AccountFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_account, container, false);

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
        ImageLoader.getInstance().init(config);

        final SliderAdapter adapter = new SliderAdapter(getActivity());

        btnSettings = rootLayout.findViewById(R.id.btn_settings);
        btnChangeDp = rootLayout.findViewById(R.id.btn_change_dp);
        btnAccountEdit = rootLayout.findViewById(R.id.btn_account_edit);
        txtAccountName = rootLayout.findViewById(R.id.txt_account_name);
        txtAccountDetails = rootLayout.findViewById(R.id.account_details);
        txtAccountAddress = rootLayout.findViewById(R.id.account_address);

        profilePhoto = rootLayout.findViewById(R.id.profile_image);
        btnSettings.setOnClickListener(this);
        btnChangeDp.setOnClickListener(this);
        btnAccountEdit.setOnClickListener(this);

        dialog = DialogPlus.newDialog(getContext())
                .setContentHolder(new ViewHolder(R.layout.fragment_user_settings))
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener((dialog, view) -> {

                    SeekBar distanceMax = dialog.getHolderView().findViewById(R.id.distance_slider);
                    SeekBar agePref = dialog.getHolderView().findViewById(R.id.age_slider);
                    TextView ageTxt = dialog.getHolderView().findViewById(R.id.txt_age);
                    TextView distanceTxt = dialog.getHolderView().findViewById(R.id.txt_distance);
                    TextView sizePref = dialog.getHolderView().findViewById(R.id.txt_pref_breed_size);
                    RadioGroup rGroup = dialog.getHolderView().findViewById(R.id.pref_breed_sizes);

                    rGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                        RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(i);

                        boolean isChecked = checkedRadioButton.isChecked();
                        if (isChecked)
                        {
                            Map userInfo = new HashMap<>();

                            switch(checkedRadioButton.getId()) {
                                case R.id.pet_breed_size_small_pref:
                                    if (isChecked)
                                    breedSizePref = "Small Breeds";
                                    break;
                                case R.id.pet_breed_size_medium_pref:
                                    if (isChecked)
                                        breedSizePref = "Medium Breeds";
                                    break;
                                case R.id.pet_breed_size_large_pref:
                                    if (isChecked)
                                        breedSizePref = "Large Breeds";
                                    break;
                                case R.id.pet_breed_size_mixed_pref:
                                    if(isChecked)
                                        breedSizePref = "Mixed Breeds";
                                    break;
                                case R.id.pet_breed_all_breeds:
                                    if(isChecked)
                                        breedSizePref = "All Breeds";
                                    break;

                            }

                            sizePref.setText("Showing: " + breedSizePref);

                            userInfo.put("size_pref", breedSizePref);

                            petRef.updateChildren(userInfo).addOnCompleteListener(task1 -> {
                                if(!task1.isSuccessful()){
                                    Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }else{
                                    return;
                                }
                            });
                        }
                    });

                    agePref.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            Map userInfo = new HashMap<>();
                            ageTxt.setText("6 months - " + seekBar.getProgress() + " years old");
                            userInfo.put("age_pref", seekBar.getProgress());

                            petRef.updateChildren(userInfo).addOnCompleteListener(task1 -> {
                                if(!task1.isSuccessful()){
                                    Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }else{
                                    return;
                                }
                            });
                        }
                    });

                    petRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                if(snapshot.hasChild("maximum_distance")){
                                    String dist = snapshot.child("maximum_distance").getValue().toString();
                                    distanceTxt.setText(dist + "km");
                                    distanceMax.setProgress(Integer.parseInt(dist));
                                }

                                if(snapshot.hasChild("age_pref")){
                                    ageTxt.setText("6 months - " + snapshot.child("age_pref").getValue().toString()+ " years old");
                                    agePref.setProgress(Integer.parseInt(snapshot.child("age_pref").getValue().toString()));
                                }

                                if(snapshot.hasChild("size_pref")){
                                    sizePref.setText(snapshot.child("size_pref").getValue().toString());

                                    switch (snapshot.child("size_pref").getValue().toString()){
                                        case "Small Breeds":
                                           rGroup.check(R.id.pet_breed_size_small_pref);
                                            break;
                                        case "Medium Breeds":
                                           rGroup.check(R.id.pet_breed_size_medium_pref);
                                            break;
                                        case "Large Breeds":
                                            rGroup.check(R.id.pet_breed_size_large_pref);
                                            break;
                                        case "Mixed Breeds":
                                            rGroup.check(R.id.pet_breed_size_mixed_pref);
                                            break;
                                    }
                                };
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    distanceMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            Map userInfo = new HashMap<>();
                            distanceTxt.setText(seekBar.getProgress() + "km");
                            userInfo.put("maximum_distance", seekBar.getProgress());

                            petRef.updateChildren(userInfo).addOnCompleteListener(task1 -> {
                                if(!task1.isSuccessful()){
                                    Toast.makeText(getContext(), task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }else{
                                    return;
                                }
                            });
                        }
                    });

                    switch (view.getId()) {
                        case R.id.btn_signout:
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(getContext(), "User Signed Out", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getContext(), StartActivity.class);
                            startActivity(i);
                            break;
                    }})
                .setExpanded(true, 900)
                .create();

        // Read pet data from database
        getPetInfo();

        // Read user data from database
        getUserInfo();

        return rootLayout;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_settings:
                v.setOnClickListener(v1 -> {
                    dialog.show();
                    return;
                });
                break;

            case R.id.btn_change_dp:
                v.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                });
                break;

            case R.id.btn_account_edit:
                v.setOnClickListener(view -> {
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    intent.putExtra("uid", userId);
                    startActivity(intent);
                    return;
                });
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getAge(LocalDate birthDate) {
        if (birthDate != null) {
            return Period.between(birthDate, LocalDate.now()).getYears();
        } else {
            return 0;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getMonth(LocalDate date) {
        if (date != null) {
            return Period.between(date, LocalDate.now()).getMonths();
        } else {
            return 0;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getPetInfo(){

        petRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    petInfo.put("pet_name", snapshot.child("pet_name").getValue().toString());
                    petInfo.put("pet_bday", snapshot.child("pet_bday").getValue().toString());
                    petInfo.put("pet_gender", snapshot.child("pet_gender").getValue().toString());
                    petInfo.put("pet_breed", snapshot.child("pet_breed").getValue().toString());
                    petInfo.put("pet_size", snapshot.child("pet_size").getValue().toString());

                    if(snapshot.hasChild("latitude") && snapshot.hasChild("longitude")) {
                        Double userLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                        Double userLong = Double.parseDouble(snapshot.child("longitude").getValue().toString());

                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(userLat, userLong, 1);
                            String brgy = addresses.get(0).getThoroughfare();
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                            txtAccountAddress.setText(city + ", " + state);

                            if(city == null && brgy != null){
                                txtAccountAddress.setText(brgy + ", " + state);
                            }else if(city == null && brgy == null && knownName != null){
                                txtAccountAddress.setText(knownName + ", " + state);
                            }else if(city == null && brgy == null && knownName == null && state == null){
                                txtAccountAddress.setText(address);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);
                    LocalDate petBday = LocalDate.parse(petInfo.get("pet_bday").toString(), formatter);

                    final int petAge = getAge(petBday) <= 0 ? getMonth(petBday) : getAge(petBday);
                    final String petAgeMeasrmnt = getAge(petBday) <= 0 ? "Months" : "Years";

                    if(snapshot.child("profileImageUrl").exists()){
                        petInfo.put("profileImage", snapshot.child("profileImageUrl").getValue().toString());

                        Log.d("profile image", petInfo.get("profileImage").toString());

                        Glide.with(getContext())
                                .load(petInfo.get("profileImage").toString())
                                .into(profilePhoto);
                    }

                    txtAccountName.setText(petInfo.get("pet_name") + ", " + petAge + " " + petAgeMeasrmnt);
                    txtAccountDetails.setText(petInfo.get("pet_gender") + ", " + petInfo.get("pet_breed"));

                    if(petInfo.get("pet_size").toString().contains("Mixed Breeds")){
                        txtAccountDetails.setText(petInfo.get("pet_gender") + ", Mixed Breed" );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserInfo(){

        userRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    userInfo.put("fname", snapshot.child("fname").getValue().toString());
                    userInfo.put("lname", snapshot.child("lname").getValue().toString());
                    userInfo.put("gender", snapshot.child("gender").getValue().toString());
                    userInfo.put("email", snapshot.child("email").getValue().toString());
                    userInfo.put("bday", snapshot.child("bday").getValue().toString());

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);
                    LocalDate bday = LocalDate.parse(userInfo.get("bday").toString(), formatter);

                    final int age = getAge(bday);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;

            if(resultUri != null){
                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
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

                        Map userInfo = new HashMap();
                        userInfo.put("profileImageUrl", uri.toString());
                        petRef.updateChildren(userInfo);
                    });


                    return;
                });
            }

            profilePhoto.setImageURI(resultUri);
        }
    }



}
