package com.example.tinpet.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinpet.R;
import com.example.tinpet.activities.MessageActivity;
import com.example.tinpet.entities.MessageItem;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder> {
    private Context context;
    private List<MessageItem> messageList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, content, count;
        ImageView thumbnail;
        RelativeLayout viewIndicator, parent;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text_name);
            content = view.findViewById(R.id.text_content);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewIndicator = view.findViewById(R.id.layout_dot_indicator);
            parent = view.findViewById(R.id.layout_message_content);

        }
    }


    public MessageListAdapter(Context context, List<MessageItem> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_message_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MessageItem item = messageList.get(position);
        holder.name.setText(item.getName());
        holder.content.setText(item.getContent());

        if(item.getCount() <= 0){
            holder.viewIndicator.setVisibility(View.INVISIBLE);
        }

        if(item.getPicture() != null){
            Glide.with(context)
                    .load(item.getPicture())
                    .into(holder.thumbnail);
        }else{
            holder.thumbnail.setImageResource(R.drawable.account_circle2);
        }

        holder.parent.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("chatID", item.getChatID());
            intent.putExtra("pet_name", item.getName());
            intent.putExtra("otherUserId", item.getId());

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

}