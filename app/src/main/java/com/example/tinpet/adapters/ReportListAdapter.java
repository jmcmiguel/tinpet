package com.example.tinpet.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinpet.R;
import com.example.tinpet.activities.CreateAdminActivity;
import com.example.tinpet.activities.MessageActivity;
import com.example.tinpet.activities.SuperAdminActivity;
import com.example.tinpet.activities.UpdateAdminActivity;
import com.example.tinpet.entities.AdminItem;
import com.example.tinpet.entities.MessageItem;
import com.example.tinpet.entities.ReportItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.MyViewHolder>{

    private Context context;
    private List<ReportItem> reportList;
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    DialogPlus dialog;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, content, count, status;
        ImageView thumbnail;
        RelativeLayout viewIndicator, parent;

        public MyViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.admin_text_name);
            content = view.findViewById(R.id.admin_text_content);
            thumbnail = view.findViewById(R.id.admin_thumbnail);
            parent = view.findViewById(R.id.admin_layout_message_content);
        }
    }

    public ReportListAdapter(Context context, List<ReportItem> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_report_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ReportItem item = reportList.get(position);

        dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.fragment_admin_view_report_details))
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener((dialog, view) -> {

                    ImageView reportPhoto = dialog.getHolderView().findViewById(R.id.report_img);
                    EditText reportType = dialog.getHolderView().findViewById(R.id.report_type);
                    EditText reportDesc = dialog.getHolderView().findViewById(R.id.report_desc);

                    reportDesc.setText(item.getReportDetails());
                    reportType.setText(item.getReportCategory());

                    if(item.getReportImageUrl() != null){
                        Glide.with(view.getContext())
                                .load(Uri.parse(item.getReportImageUrl()))
                                .into(reportPhoto);
                    }

                    switch (view.getId()) {
                        case R.id.fragment_btn_disable:

                            Map<String, Object> data = new HashMap<>();
                            data.put("uid", item.getReportee());

                            disableUser(data)
                                    .addOnCompleteListener(task -> {

                                        if (!task.isSuccessful()) {
                                            Exception e = task.getException();
                                            if (e instanceof FirebaseFunctionsException) {
                                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                                FirebaseFunctionsException.Code code = ffe.getCode();
                                                Object details = ffe.getDetails();
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        Toast.makeText(context, "Account Disabled. It may take at least an hour to take effect.", Toast.LENGTH_LONG).show();

                                        return;
                                    });

                            break;
                    }})
                .setExpanded(true, 900)  // This will enable the expand feature, (similar to android L share dialog)
                .create();


        dbRef.child("Users/" + item.getReportee() + "/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.name.setText(snapshot.child("fname").getValue().toString() + " " + snapshot.child("lname").getValue().toString());

                    dbRef.child("Users/" + item.getReporter()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if(snapshot2.exists()){
                                holder.content.setText("Reported By: " + snapshot2.child("fname").getValue().toString() + " " + snapshot2.child("lname").getValue().toString());
                            }else{
                                holder.content.setText("Reported By Anonymous User");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    holder.thumbnail.setImageResource(R.drawable.account_circle2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.parent.setOnClickListener(view -> {
            dialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    private Task<String> disableUser(Map<String, Object> data) {
        return mFunctions
                .getHttpsCallable("disableUser")
                .call(data)
                .continueWith(task -> {
                    String result = (String) task.getResult().getData();
                    return result;
                });
    }
}
