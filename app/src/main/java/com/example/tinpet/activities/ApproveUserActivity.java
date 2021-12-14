package com.example.tinpet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinpet.R;
import com.example.tinpet.adapters.ApproveUserAdapter;
import com.example.tinpet.adapters.ReportListAdapter;
import com.example.tinpet.entities.ApproveUserItem;
import com.example.tinpet.entities.ReportItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ApproveUserActivity extends AppCompatActivity {

    Button back;
    private List<ApproveUserItem> approvalItems;
    private ApproveUserAdapter approveUserAdapter;
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_user);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_approve_users);
        approvalItems = new ArrayList<>();
        approveUserAdapter = new ApproveUserAdapter(ApproveUserActivity.this, approvalItems);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(approveUserAdapter);

        back = findViewById(R.id.btn_approve_user_back);

        back.setOnClickListener(view -> {
            Intent intent1 = getIntent();
            String activity = intent1.getStringExtra("activity");
            if(activity == null) {
                Intent intent = new Intent(this, SuperAdminActivity.class);
                startActivity(intent);
                finish();
            }else if(activity.equals("admin1")){
                Intent intent = new Intent(this, Admin1Activity.class);
                startActivity(intent);
                finish();
            }else if(activity.equals("admin2")){
                Intent intent = new Intent(this, Admin2Activity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(this, SuperAdminActivity.class);
                startActivity(intent);
                finish();
            }

            return;
        });

        prepareUsers();
    }

    private void prepareUsers(){
        DatabaseReference users = dbRef.child("Users/");

        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists() && !snapshot.hasChild("approved") && !snapshot.hasChild("adminType")){
                    String uid = snapshot.getKey();
                    String name = snapshot.child("fname").getValue().toString() + " " + snapshot.child("lname").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    String gender = snapshot.child("gender").getValue().toString();
                    String bday = snapshot.child("bday").getValue().toString();

                    String medcert = null;
                    String validID = null;

                    if(snapshot.hasChild("validID")){
                        validID = snapshot.child("validID").getValue().toString();
                    }

                    if(snapshot.hasChild("medCert")){
                        medcert = snapshot.child("medCert").getValue().toString();
                    }

                    ApproveUserItem userItem = new ApproveUserItem(uid, name, email, gender, bday, medcert, validID);
                    approvalItems.add(userItem);
                    approveUserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if(snapshot.hasChild("approved")){
                    approvalItems.removeIf(obj -> obj.getUid() == snapshot.getKey());
                    approveUserAdapter.notifyDataSetChanged();
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
}
