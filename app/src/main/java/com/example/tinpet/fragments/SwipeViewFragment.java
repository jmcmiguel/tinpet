package com.example.tinpet.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tinpet.entities.Match;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.example.tinpet.R;
import com.example.tinpet.Utils;
import com.example.tinpet.entities.Profile;
import com.example.tinpet.entities.TinderCard;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SwipeViewFragment extends Fragment {


    private View rootLayout;
    private FloatingActionButton fabLike, fabSkip;

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    FirebaseAuth firebaseAuth;
    DatabaseReference petsRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Pets");
    private String petSex;
    private String oppositePetSex;
    final FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
    Double userLat;
    Double userLong;
    Double maxDistance;
    Double agePref;
    String sizePref;

    public SwipeViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_swipe_view, container, false);

        return rootLayout;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeView = view.findViewById(R.id.swipeView);
        fabLike = view.findViewById(R.id.fabLike);
        fabSkip = view.findViewById(R.id.fabSkip);

        mContext = getActivity();

        int bottomMargin = Utils.dpToPx(100);
        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        petsRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("latitude") && snapshot.hasChild("longitude")){
                        userLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                        userLong = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                    }

                    if(snapshot.hasChild("maximum_distance")){
                        maxDistance = Double.parseDouble(snapshot.child("maximum_distance").getValue().toString());
                    }

                    if(snapshot.hasChild("age_pref")){
                        agePref = Double.parseDouble(snapshot.child("age_pref").getValue().toString());
                    }

                    if(snapshot.hasChild("size_pref")){
                        sizePref = snapshot.child("size_pref").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Get the sex of pet
        checkPetSex();

        // Get Pets of the opposite sex
        getOppositeSexPets();

        fabSkip.setOnClickListener(v -> {
            animateFab(fabSkip);
            mSwipeView.doSwipe(false);
        });

        fabLike.setOnClickListener(v -> {
            animateFab(fabLike);
            mSwipeView.doSwipe(true);
        });

    }

    private void animateFab(final FloatingActionButton fab){
        fab.animate().scaleX(0.7f).setDuration(100).withEndAction(() -> fab.animate().scaleX(1f).scaleY(1f));
    }

    public void checkPetSex(){
        DatabaseReference petRef = petsRef.child(user.getUid());
        petRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("pet_gender").getValue() != null){
                        petSex = dataSnapshot.child("pet_gender").getValue().toString();
                        switch (petSex){
                            case "Male":
                                oppositePetSex = "Female";
                                break;
                            case "Female":
                                oppositePetSex = "Male";
                                break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeSexPets(){

        petsRef.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Double otLat = null;
                    Double otLong = null;
                    String sizePref = null;

                    if(snapshot.hasChild("latitude") && snapshot.hasChild("longitude")){
                        otLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                        otLong = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                    }

                    Log.d("thisuser", snapshot.toString());

                    // Get other pets if opposite user and not disliked, liked, or matched yet by authenticated user
                    if(snapshot.hasChild("pet_gender") &&  snapshot.child("pet_gender").getValue().toString().equals(oppositePetSex) &&
                       !snapshot.child("Swipes/Like").hasChild(user.getUid()) &&
                       !snapshot.child("Swipes/Dislike").hasChild(user.getUid()) &&
                        withinPreferredDistance(otLat, otLong) &&
                        withinAgePref(snapshot.child("pet_bday").getValue().toString()) &&
                        withinSizePref(snapshot.child("pet_size").getValue().toString())){

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);
                        LocalDate petBday = LocalDate.parse(snapshot.child("pet_bday").getValue().toString(), formatter);

                        Profile profile = new Profile();
                        profile.setName(snapshot.child("pet_name").getValue().toString());
                        profile.setAge(getAge(petBday));

                        String imageURL;

                        if (snapshot.child("profileImageUrl").exists())
                            imageURL = snapshot.child("profileImageUrl").getValue().toString();
                        else imageURL = null;

                        if(snapshot.child("latitude").exists() && snapshot.child("longitude").exists()){
                            profile.setLatitude(snapshot.child("latitude").getValue().toString());
                            profile.setLongitude(snapshot.child("longitude").getValue().toString());
                        }else{
                            profile.setLocation("N/A");
                        }

                        profile.setImageUrl(imageURL);
                        profile.setBreed(snapshot.child("pet_breed").getValue().toString());
                        profile.setGender(snapshot.child("pet_gender").getValue().toString());
                        profile.setUid(snapshot.getKey());
                        profile.setSize(snapshot.child("pet_size").getValue().toString());

                        if(snapshot.hasChild("pet_breed2"))
                            profile.setBreed2(snapshot.child("pet_breed2").getValue().toString());

                        if(snapshot.hasChild("pet_breed3"))
                            profile.setBreed3(snapshot.child("pet_breed3").getValue().toString());

                        if(snapshot.hasChild("pet_breed4"))
                            profile.setBreed4(snapshot.child("pet_breed4").getValue().toString());

                        if(snapshot.hasChild("pet_breed5"))
                            profile.setBreed5(snapshot.child("pet_breed5").getValue().toString());


                        mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
                    }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    if(snapshot.getKey().equals(user.getUid()) &&  snapshot.hasChild("maximum_distance")){
                        Log.d("childchanged", snapshot.toString());

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        if (Build.VERSION.SDK_INT >= 26) {
                            ft.setReorderingAllowed(false);
                        }
                        ft.detach(SwipeViewFragment.this).attach(SwipeViewFragment.this).commitAllowingStateLoss();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    /** calculates the distance between two locations in KM */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        DecimalFormat df = new DecimalFormat("0.00");


        return Double.parseDouble(df.format(dist));
    }

    private boolean withinPreferredDistance(Double latitude, Double longitude){

        if(latitude == null || longitude == null || maxDistance == null ||
           userLat == null || userLong == null) return false;

        Double distanceBetween = calculateDistance(latitude, longitude, userLat, userLong);

        if(distanceBetween <= maxDistance){
            return true;
        }else{
            return false;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean withinAgePref(String otherPetBday){
        if(otherPetBday == null || agePref == null) return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);
        LocalDate petBday = LocalDate.parse(otherPetBday, formatter);
        final int petAge = getAge(petBday);

        if(petAge <= agePref) return true;

        return false;
    }

    private boolean withinSizePref(String ds){

        if(ds == null || sizePref == null) return false;

        if(sizePref.equals(ds)){
            return true;
        }else if(sizePref.equals("Mixed Breeds") && ds.contains("Mixed Breeds")){
            return true;
        }

        return false;
    }

}
