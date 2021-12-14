package com.example.tinpet.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinpet.R;
import com.example.tinpet.activities.MessageActivity;
import com.example.tinpet.activities.UpdateAdminActivity;
import com.example.tinpet.entities.AdminItem;
import com.example.tinpet.entities.MessageItem;

import java.util.List;

public class AdminListAdapter extends RecyclerView.Adapter<AdminListAdapter.MyViewHolder>{

    private Context context;
    private List<AdminItem> adminList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, content, count;
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

    public AdminListAdapter(Context context, List<AdminItem> adminList) {
        this.context = context;
        this.adminList = adminList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_admin_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final AdminItem item = adminList.get(position);

        holder.name.setText(item.getName() + " (" + item.getAdminType() + ")");
        holder.content.setText(item.getEmail());
        holder.thumbnail.setImageResource(R.drawable.account_circle2);

        holder.parent.setOnClickListener(view -> {
            Intent intent = new Intent(context, UpdateAdminActivity.class);
            intent.putExtra("uid", item.getUid());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }
}
