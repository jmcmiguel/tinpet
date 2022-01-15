package com.example.tinpet.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.tinpet.activities.EmailRegisterActivity;
import com.example.tinpet.activities.MessageActivity;
import com.example.tinpet.activities.SuperAdminActivity;
import com.example.tinpet.activities.UpdateAdminActivity;
import com.example.tinpet.entities.AdminItem;
import com.example.tinpet.entities.ApproveUserItem;
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

public class ApproveUserAdapter extends RecyclerView.Adapter<ApproveUserAdapter.MyViewHolder>{

    private Context context;
    private List<ApproveUserItem> approveUserItemList;
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

    public ApproveUserAdapter(Context context, List<ApproveUserItem> reportList) {
        this.context = context;
        this.approveUserItemList = reportList;
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

        final ApproveUserItem item = approveUserItemList.get(position);

        dialog = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.fragment_admin_view_user_approval))
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener((dialog, view) -> {

                    ImageView medCert = dialog.getHolderView().findViewById(R.id.medcert_img);
                    Button approveUser = dialog.getHolderView().findViewById(R.id.btn_user_approve);
                    Button rejectUser = dialog.getHolderView().findViewById(R.id.btn_user_reject);
                    EditText username = dialog.getHolderView().findViewById(R.id.user_name);
                    EditText useremail = dialog.getHolderView().findViewById(R.id.user_email);
                    EditText usergender = dialog.getHolderView().findViewById(R.id.user_gender);
                    EditText userbday = dialog.getHolderView().findViewById(R.id.user_bday);

                    Log.d("loglog", item.toString());

                    username.setText(item.getName());
                    useremail.setText(item.getEmail());
                    usergender.setText(item.getGender());
                    userbday.setText(item.getBday());

                    username.setEnabled(false);
                    useremail.setEnabled(false);
                    usergender.setEnabled(false);
                    userbday.setEnabled(false);

                    if(item.getMedcert() != null){
                        Glide.with(context)
                                .load(Uri.parse(item.getMedcert()))
                                .into(medCert);
                    }

                    // Approve User Button Listener
                    approveUser.setOnClickListener(view1 -> {
                        Map info = new HashMap<>();
                        String usernode = "Users/" + item.getUid() + "/";
                        info.put(usernode + "approved", true);

                        dbRef.updateChildren(info).addOnCompleteListener(task -> {
                            if(!task.isSuccessful()){
                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "User Approved", Toast.LENGTH_SHORT).show();
                            }
                        });

                    });

                    // Reject User Button Listener
                    rejectUser.setOnClickListener(view2 -> {

                        Map<String, Object> data = new HashMap<>();
                        data.put("uid", item.getUid());

                        rejectUser(data)
                                .addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        Exception e = task.getException();
                                        if (e instanceof FirebaseFunctionsException) {
                                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                            FirebaseFunctionsException.Code code = ffe.getCode();
                                            Object details = ffe.getDetails();
                                            Toast.makeText(context, details.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    Toast.makeText(context, "Rejected User", Toast.LENGTH_SHORT).show();
                                });
                    });

                })
                .setExpanded(true, 900)
                .create();

        holder.name.setText(item.getName());
        holder.thumbnail.setImageResource(R.drawable.account_circle2);
        holder.content.setText(item.getEmail());

         holder.parent.setOnClickListener(view -> {
             dialog.show();
         });
    }

    @Override
    public int getItemCount() {
        return approveUserItemList.size();
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

    private Task<String> rejectUser(Map<String, Object> data) {
        return mFunctions
                .getHttpsCallable("rejectUser")
                .call(data)
                .continueWith(task -> {
                    String result = (String) task.getResult().getData();
                    return result;
                });
    }
}
