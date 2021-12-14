package com.example.tinpet.fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tinpet.R;
import com.example.tinpet.activities.MainActivity;
import com.example.tinpet.adapters.MatchListAdapter;
import com.example.tinpet.entities.Match;
import com.example.tinpet.entities.MessageItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    View rootLayout;
    private List<Match> matchList;
    private MatchListAdapter mAdapter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    String userId = firebaseAuth.getCurrentUser().getUid();
    LinearLayout emptyAnimation;
    String distanceText = "";
    Double userLat;
    Double userLong;
    Double otherUserLat;
    Double otherUserLong;
    Double distance;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_feed, container, false);

        RecyclerView recyclerView = rootLayout.findViewById(R.id.recycler_view_matchs);
        matchList = new ArrayList<>();
        mAdapter = new MatchListAdapter(getContext(), matchList);

        emptyAnimation = rootLayout.findViewById(R.id.emptyFeedAnimation);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        prepareMatchList();

//        if(mAdapter.getItemCount() == 0){
//            emptyAnimation.setVisibility(View.VISIBLE);
//        }else if(mAdapter.getItemCount() > 0){
//            emptyAnimation.setVisibility(View.GONE);
//        }
        
        return rootLayout;
    }


    private void prepareMatchList(){
        DatabaseReference currentDbRef = dbRef.child("Pets/"+ userId +"/Swipes/Matches");

        currentDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Long rawDate = (Long) snapshot.child("date").getValue();
                String timeAgo = (String) DateUtils.getRelativeTimeSpanString((Long) snapshot.child("date").getValue());
                String chatID = snapshot.child("chat").getValue().toString();
                String otherUserID = snapshot.getKey();

                dbRef.child("Pets/" + userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists() && snapshot.hasChild("latitude") && snapshot.hasChild("longitude")){
                            userLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                            userLong = Double.parseDouble(snapshot.child("longitude").getValue().toString());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dbRef.child("Pets/" + snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String name = snapshot.child("pet_name").getValue().toString();
                            String breed = snapshot.child("pet_breed").getValue().toString();
                            String bday = snapshot.child("pet_bday").getValue().toString();
                            String imageURL;
                            String petSize = snapshot.child("pet_size").getValue().toString();

                            if (snapshot.child("profileImageUrl").exists())
                                imageURL = snapshot.child("profileImageUrl").getValue().toString();
                            else imageURL = null;

                            if(snapshot.exists() && snapshot.hasChild("latitude") && snapshot.hasChild("longitude")){
                                otherUserLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                                otherUserLong = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                                distance = calculateDistance(userLat,userLong, otherUserLat, otherUserLong);
                            }else{
                                distance = null;
                            }

                            if(distance == null){
                                distanceText = "N/A";
                            }else if(distance == 0 || distance < 0){
                                distanceText = "< 1 km away";
                            }else{
                                distanceText = distance + " km away";
                            }

                            Match match = new Match(name, imageURL, distanceText, timeAgo, chatID, otherUserID,rawDate, breed, bday, petSize);
                            matchList.add(match);
                            matchList.sort(Comparator.comparing(Match::getRawDate).reversed());
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
