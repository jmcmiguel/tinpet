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

public class Admin2Activity extends AppCompatActivity {

    Button logout, viewReports;
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_2);

        logout = findViewById(R.id.btn_super_admin_logout);
        viewReports = findViewById(R.id.btn_view_reports);

        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this , "Super Admin Signed Out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this , StartActivity.class);
            startActivity(i);
            return;
        });

        viewReports.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewReportsActivity.class);
            intent.putExtra("activity","admin2");
            startActivity(intent);
            finish();
            return;
        });
    }
}
