package com.example.tinpet;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.tinpet.entities.Profile;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Utils {

    private static final String TAG = "Utils";
    FirebaseAuth firebaseAuth;
    private static DatabaseReference petsRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Pets");
    private static String petSex;
    private static String oppositePetSex;
    final static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

    public static void checkPetSex(){
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
                        getOppositeSexPets();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getOppositeSexPets(){
        petsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("pet_gender").getValue() != null) {
                    if (dataSnapshot.exists()) {
                        Log.d("firebase", dataSnapshot.child("pet_name").getValue().toString());
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json;
        InputStream is;
        try {
            AssetManager manager = context.getAssets();
            Log.d(TAG,"path "+jsonFileName);
            is = manager.open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static Point getDisplaySize(WindowManager windowManager){
        try {
            if(Build.VERSION.SDK_INT > 16) {
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
            }else{
                return new Point(0, 0);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new Point(0, 0);
        }
    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}