package com.example.tinpet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinpet.R;
import com.example.tinpet.adapters.AdminListAdapter;
import com.example.tinpet.adapters.MessageListAdapter;
import com.example.tinpet.entities.AdminItem;
import com.example.tinpet.entities.MessageItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SuperAdminActivity extends AppCompatActivity {

    Button logout, createAdmin;
    private List<AdminItem> adminList;
    private AdminListAdapter adminListAdapter;
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_admins);
        adminList = new ArrayList<>();
        adminListAdapter = new AdminListAdapter(SuperAdminActivity.this, adminList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adminListAdapter);

        prepareAdminList();

        logout = findViewById(R.id.btn_super_admin_logout);
        createAdmin = findViewById(R.id.btn_create_admin);

        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this , "Super Admin Signed Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this , StartActivity.class);
            startActivity(i);
            return;
        });

        createAdmin.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateAdminActivity.class);
            startActivity(intent);
            finish();
            return;
        });
    }

    private void prepareAdminList() {
        DatabaseReference adminRef = dbRef.child("Admins");

        adminRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){

                    String email = snapshot.child("email").getValue().toString();
                    String name = snapshot.child("name").getValue().toString();
                    String password = snapshot.child("adminType").getValue().toString();
                    String adminType = snapshot.child("adminType").getValue().toString();
                    String uid = snapshot.getKey();

                    AdminItem admin = new AdminItem(uid,email, name, password, adminType);
                    Log.d("admin", String.valueOf(admin.getEmail()));
                    adminList.add(admin);
                    adminListAdapter.notifyDataSetChanged();
                }
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
}
