package com.example.tinpet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.tinpet.R;
import com.example.tinpet.adapters.ViewPagerAdapter;
import com.example.tinpet.fragments.AccountFragment;
import com.example.tinpet.fragments.ActivityFragment;
import com.example.tinpet.fragments.SwipeViewFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Context mContext;
    private ViewPager viewPager;
    private LocationManager locationManager;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    String userId = firebaseAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);

        ArrayList<Fragment> fragList = new ArrayList<>();
        fragList.add(new AccountFragment());
        fragList.add(new SwipeViewFragment());
        fragList.add(new ActivityFragment());
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(fragList, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        bnv.setOnNavigationItemSelectedListener(this);

        // Set default selected nav bar to matching
        bnv.setSelectedItemId(R.id.fire);
        viewPager.setCurrentItem(1);

        // Get GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // if location permission is denied
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    String petNode = "Pets/" + userId + "/";

                    Map userLocation = new HashMap<>();
                    userLocation.put(petNode + "longitude", location.getLongitude());
                    userLocation.put(petNode + "latitude", location.getLatitude());

                    databaseReference.updateChildren(userLocation).addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("location", "location updated to firebase");
                        }
                    });

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.account:
                viewPager.setCurrentItem(0);
                break;
            case R.id.fire:
                viewPager.setCurrentItem(1);
                break;
            case R.id.chat:
                viewPager.setCurrentItem(2);
                break;
        }
        return true;
    }

}