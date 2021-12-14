package com.example.tinpet.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinpet.R;
import com.example.tinpet.entities.Match;
import com.example.tinpet.activities.MessageActivity;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.MyViewHolder> {
    private Context context;
    private List<Match> matchList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, location, breed;
        ImageView imgProfile, imgContent, btnChat;
        Spinner breedSpinner;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text_name);
            date = view.findViewById(R.id.text_date);
            location = view.findViewById(R.id.text_location);
            imgProfile = view.findViewById(R.id.img_profile);
            imgContent = view.findViewById(R.id.img_content);
            btnChat = view.findViewById(R.id.img_chat);
            breed = view.findViewById(R.id.match_breed_text);
            breedSpinner = view.findViewById(R.id.breedSpnr);

        }
    }


    public MatchListAdapter(Context context, List<Match> matchList) {
        this.context = context;
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_layout_match, parent, false);

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Match item = matchList.get(position);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);
        LocalDate bday = LocalDate.parse(item.getBday(), formatter);

        holder.name.setText(item.getName() + ", " + getAge(bday));
        holder.date.setText(item.getDate());
        holder.location.setText(item.getLocation());
        holder.breed.setText(item.getBreed());
        holder.imgContent.setBackgroundResource(R.color.md_deep_orange_800);
        holder.imgProfile.setImageResource(R.drawable.account_circle2);

        if(item.getPetSize().contains("Mixed Breeds")){
            holder.breed.setText(item.getPetSize());
        }

        if(item.getPicture() != null){
            Glide.with(context)
                    .load(item.getPicture())
                    .into(holder.imgProfile);

            Glide.with(context)
                    .load(item.getPicture())
                    .into(holder.imgContent);
        }

        holder.btnChat.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("chatID", item.getChatID());
            intent.putExtra("otherUserId", item.getOtherUserID());
            intent.putExtra("pet_name", item.getName());
            Log.d("chatid", item.getChatID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getAge(LocalDate birthDate) {
        if (birthDate != null) {
            return Period.between(birthDate, LocalDate.now()).getYears();
        } else {
            return 0;
        }
    }


}