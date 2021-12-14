package com.example.tinpet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinpet.R;
import com.example.tinpet.adapters.AdminListAdapter;
import com.example.tinpet.adapters.ReportListAdapter;
import com.example.tinpet.entities.ReportItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ViewReportsActivity extends AppCompatActivity {

    Button back;
    private List<ReportItem> reportItems;
    private ReportListAdapter reportListAdapter;
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_reports);
        reportItems = new ArrayList<>();
        reportListAdapter = new ReportListAdapter(ViewReportsActivity.this, reportItems);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reportListAdapter);

        prepareReportsList();

        back = findViewById(R.id.btn_view_reports_back);

        back.setOnClickListener(view -> {
            Intent intent1 = getIntent();
            String activity = intent1.getStringExtra("activity");
            if(activity == null) {
                Intent intent = new Intent(this, SuperAdminActivity.class);
                startActivity(intent);
                finish();
            }
            else if(activity.equals("admin1")){
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
        });
    }

    private void prepareReportsList(){
        DatabaseReference reportsRef = dbRef.child("Reports");

        reportsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    Log.d("admin", snapshot.toString());

                    String reportee = snapshot.getKey().toString();
                    String reporter = snapshot.child("reported_by").getValue().toString();
                    String reported_at =  snapshot.child("reported_at").getValue().toString();
                    String report_details = snapshot.child("report_details").getValue().toString();
                    String report_category = snapshot.child("report_category").getValue().toString();
                    String report_img = null;

                    if(snapshot.hasChild("reportImageUrl")){
                        report_img = snapshot.child("reportImageUrl").getValue().toString();
                    }

                    ReportItem reportItem = new ReportItem(reportee, reporter, reported_at, report_details, report_category, report_img);
                    reportItems.add(reportItem);
                    reportListAdapter.notifyDataSetChanged();
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
