package com.example.tinpet.entities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.tinpet.activities.PetRegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.example.tinpet.R;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Layout(R.layout.adapter_tinder_card)
public class TinderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.breedNameTxt)
    private TextView breedNameTxt;

    @View(R.id.genderTxt)
    private TextView genderTxt;

    @View(R.id.locationTxt)
    private TextView locationTxt;

    @View(R.id.breedNameSpnr)
    private Spinner breedSpnr;

    private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    String userId = firebaseAuth.getCurrentUser().getUid();
    String petSwipesNode = "Pets/";

    public TinderCard(Context context, Profile profile, SwipePlaceHolderView swipeView) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
    }

    @SuppressLint("ResourceAsColor")
    @Resolve
    private void onResolved(){

        if(mProfile.getImageUrl() != null){
            Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        }else{
            profileImageView.setBackgroundColor(R.color.md_deep_orange_800);
            profileImageView.setBackgroundResource(R.drawable.account_circle2);
        }

        nameAgeTxt.setText(mProfile.getName() + ", " + mProfile.getAge());
        breedNameTxt.setText(mProfile.getBreed());
        genderTxt.setText(mProfile.getGender());

        ArrayList<String> arraySpinner = new ArrayList<String>();

        if(mProfile.getSize().contains("Mixed Breeds")){
            breedNameTxt.setVisibility(android.view.View.GONE);
            breedSpnr.setVisibility(android.view.View.VISIBLE);

            arraySpinner.add("Mixed Breeds") ;

            if(mProfile.getSize().equals("Mixed Breeds (2 Breeds)")){
                arraySpinner.add(mProfile.getBreed());
                arraySpinner.add(mProfile.getBreed2());
            }else if(mProfile.getSize().equals("Mixed Breeds (3 Breeds)")){
                arraySpinner.add(mProfile.getBreed());
                arraySpinner.add(mProfile.getBreed2());
                arraySpinner.add(mProfile.getBreed3());
            }else if(mProfile.getSize().equals("Mixed Breeds (4 Breeds)")){
                arraySpinner.add(mProfile.getBreed());
                arraySpinner.add(mProfile.getBreed2());
                arraySpinner.add(mProfile.getBreed3());
                arraySpinner.add(mProfile.getBreed4());
            }else if(mProfile.getSize().equals("Mixed Breeds (5 Breeds)")){
                arraySpinner.add(mProfile.getBreed());
                arraySpinner.add(mProfile.getBreed2());
                arraySpinner.add(mProfile.getBreed3());
                arraySpinner.add(mProfile.getBreed4());
                arraySpinner.add(mProfile.getBreed5());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, arraySpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            breedSpnr.setAdapter(adapter);

        }

        if(mProfile.getLatitude() != null && mProfile.getLongitude() != null){
            Double userLat = Double.parseDouble(mProfile.getLatitude());
            Double userLong = Double.parseDouble(mProfile.getLongitude());

            dbRef.child("Pets/" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists() && snapshot.hasChild("latitude") && snapshot.hasChild("longitude")){
                        Double otherUserLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                        Double otherUserLong = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                        Double distance = calculateDistance(userLat,userLong, otherUserLat, otherUserLong);

                        if(distance == 0 || distance < 0){
                            locationTxt.setText("< 1 km away");
                        }else{
                            locationTxt.setText(distance + " km away");
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else{
            locationTxt.setText(mProfile.getLocation());
        }

    }

    @SwipeOut
    private void onSwipedOut(){
        Map info = new HashMap<>();
        info.put(petSwipesNode + mProfile.getUid() + "/Swipes/Dislike/" +userId,  true);
        dbRef.updateChildren(info);

        Log.d("EVENT", "onSwipedOut");
    }

    @SwipeIn
    private void onSwipeIn(){
        Map info = new HashMap<>();
        info.put(petSwipesNode + mProfile.getUid() + "/Swipes/Like/" +userId,  true);
        dbRef.updateChildren(info);

        isConnectionMatch();

        Log.d("EVENT", "onSwipedOut");
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }

    private void isConnectionMatch() {
        DatabaseReference currentDbRef = dbRef.child("Pets/"+ userId +"/Swipes/Like/" + mProfile.getUid());

        currentDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    Toast.makeText(mContext, "It's a match!", Toast.LENGTH_LONG).show();

                    String chatKey = dbRef.child("Chat").push().getKey();
                    Long timeMatched = System.currentTimeMillis();

                    Map updates = new HashMap<>();
                    updates.put("Pets/"+ userId +"/Swipes/Matches/" + mProfile.getUid() + "/date", timeMatched);
                    updates.put("Pets/"+ userId +"/Swipes/Matches/" + mProfile.getUid() + "/chat", chatKey);
                    updates.put("Pets/" + mProfile.getUid() + "/Swipes/Matches/" + userId + "/date", timeMatched);
                    updates.put("Pets/" + mProfile.getUid() + "/Swipes/Matches/" + userId + "/chat",chatKey);

                    dbRef.updateChildren(updates);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
}