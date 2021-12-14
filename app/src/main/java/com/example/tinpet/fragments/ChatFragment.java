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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tinpet.R;
import com.example.tinpet.activities.MainActivity;
import com.example.tinpet.adapters.LikeAdapter;
import com.example.tinpet.adapters.MessageListAdapter;
import com.example.tinpet.entities.Like;
import com.example.tinpet.entities.Match;
import com.example.tinpet.entities.MessageItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    View rootLayout;
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<MessageItem> messageList;
    private List<Like> likeList;
    private MessageListAdapter mAdapter;
    private LikeAdapter contactAdapter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    String userId = firebaseAuth.getCurrentUser().getUid();
    TextView chatCount;
    LinearLayout emptyAnimation;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = rootLayout.findViewById(R.id.recycler_view_messages);
        messageList = new ArrayList<>();
        mAdapter = new MessageListAdapter(getContext(), messageList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        chatCount = rootLayout.findViewById(R.id.text_count_messsage);
        emptyAnimation = rootLayout.findViewById(R.id.emptyChatAnimation);

        prepareMessageList();
        prepareContactList();
        contactAdapter = new LikeAdapter(getContext(), likeList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerViewContact =  rootLayout.findViewById(R.id.recycler_view_likes);
        recyclerViewContact.setLayoutManager(layoutManager);
        recyclerViewContact.setAdapter(contactAdapter);
        //new HorizontalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(recyclerViewContact));

//        if(mAdapter.getItemCount() == 0){
//            emptyAnimation.setVisibility(View.VISIBLE);
//        }else if(mAdapter.getItemCount() > 0){
//            emptyAnimation.setVisibility(View.GONE);
//        }

        return rootLayout;
    }


    private void prepareMessageList(){
        DatabaseReference currentDbRef = dbRef.child("Pets/" + userId + "/Swipes/Matches" );

        currentDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){

                    String id = snapshot.getKey();
                    String chatID = snapshot.child("chat").getValue().toString();

                    dbRef.child("Conversations/" + chatID).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                            if(snapshot1.exists()){
                                String nameID = snapshot.getKey();

                                dbRef.child("Pets/" + nameID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){

                                            String pet_name = snapshot.child("pet_name").getValue().toString();
                                            String imageURL;

                                            if (snapshot.child("profileImageUrl").exists())
                                                imageURL = snapshot.child("profileImageUrl").getValue().toString();
                                            else imageURL = null;

                                            dbRef.child("Conversations/" + chatID).limitToLast(1).addChildEventListener(new ChildEventListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void onChildAdded(@NonNull DataSnapshot snapshot3, @Nullable String previousChildName) {
                                                    if(snapshot3.exists()){
                                                        Log.d("snapshot", String.valueOf(snapshot3.child("content")));

                                                        String sentBy = snapshot3.child("sent_by").getValue().toString();
                                                        String content = snapshot3.child("content").getValue().toString();
                                                        Long dateSent = (Long) snapshot3.child("date_sent").getValue();

                                                        if(sentBy.equals(firebaseAuth.getUid())) {
                                                            content = "You: " + snapshot3.child("content").getValue().toString();
                                                        }


                                                        MessageItem message = new MessageItem(id, pet_name, content, 1, imageURL, chatID, dateSent);

                                                        // If chatID already exists in messageList, remove and update
                                                        if(containsChatID(messageList, chatID)){
                                                            messageList.removeIf(msg -> msg.getChatID().equals(chatID));
                                                        }

                                                        messageList.add(message);
                                                        messageList.sort(Comparator.comparing(MessageItem::getDateSent).reversed());
                                                        chatCount.setText(String.valueOf(messageList.size()));
                                                        mAdapter.notifyDataSetChanged();
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

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void prepareContactList(){
        likeList = new ArrayList<>();
        DatabaseReference currentDbRef = dbRef.child("Pets/"+ userId +"/Swipes/Matches");

        currentDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){

                    Long rawDate = (Long) snapshot.child("date").getValue();

                    dbRef.child("Pets/" + snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String name = snapshot.child("pet_name").getValue().toString();
                                String imageURL;

                                if (snapshot.child("profileImageUrl").exists())
                                    imageURL = snapshot.child("profileImageUrl").getValue().toString();
                                else imageURL = null;

                                Log.d("snap-chat", snapshot.toString());

                                String chatID = snapshot.child("Swipes/Matches/" + userId +"/chat").getValue().toString();
                                Like like = new Like(name, chatID, imageURL, rawDate);
                                likeList.add(like);
                                likeList.sort(Comparator.comparing(Like::getRawDate).reversed());
                                contactAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

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

    public Object getLastElement(final Iterable c) {
        final Iterator itr = c.iterator();
        Object lastElement = itr.next();
        while(itr.hasNext()) lastElement = itr.next();
        return lastElement;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean containsChatID(final List<MessageItem> list, final String chatID){
        return list.stream().anyMatch(o -> o.getChatID().equals(chatID));
    }


}
