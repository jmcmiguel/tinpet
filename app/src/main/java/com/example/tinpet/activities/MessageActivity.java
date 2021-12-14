package com.example.tinpet.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.tinpet.R;
import com.example.tinpet.entities.Author;
import com.example.tinpet.entities.Message;
import com.example.tinpet.fragments.ChatFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity  {

    View rootLayout;
    private List<Message> messagesList;
    private MessagesListAdapter<Message> mAdapter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    FirebaseUser authCurrentUser = firebaseAuth.getCurrentUser();
    Author user = new Author(authCurrentUser.getUid(), authCurrentUser.getDisplayName(), "", true);
    Toolbar toolbar;
    DialogPlus dialog;
    Button report;
    String userId = firebaseAuth.getCurrentUser().getUid();
    DatabaseReference userRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Users").child(userId);
    DatabaseReference petRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Pets").child(userId);
    String otherUserId;

    private Uri resultUri;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_message);

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MessageActivity.this).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        toolbar = findViewById(R.id.msg_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messagesList = new ArrayList<Message>();
        MessagesList msgList = findViewById(R.id.messagesList);
        MessageInput inputView = findViewById(R.id.chatInput);

        ImageLoader imageLoader = (imageView, url, payload) -> {
              Glide.with(MessageActivity.this)
                        .load(url)
                        .into(imageView);
        };

        mAdapter = new MessagesListAdapter<>(authCurrentUser.getUid(), imageLoader);
        msgList.setAdapter(mAdapter);

        dialog = DialogPlus.newDialog(MessageActivity.this)
                .setContentHolder(new ViewHolder(R.layout.fragment_report_details))
                .setGravity(Gravity.BOTTOM)
                .setOnClickListener((dialog, view) -> {

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
                    String userNode = "Reports/" + otherUserId;
                    ImageView reportPhoto = findViewById(R.id.report_img);
                    Spinner reportType = dialog.getHolderView().findViewById(R.id.report_type);
                    EditText reportDesc = dialog.getHolderView().findViewById(R.id.report_desc);

                    dbRef.child(userNode).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){

                                if(snapshot.hasChild("reportImageUrl")){
                                    Glide.with(view.getContext())
                                            .load(Uri.parse(snapshot.child("reportImageUrl").getValue().toString()))
                                            .into(reportPhoto);
                                }

                                if(snapshot.hasChild("report_category")){
                                    for (int i=0;i<reportType.getCount();i++){
                                        if (reportType.getItemAtPosition(i).toString().equalsIgnoreCase(snapshot.child("report_category").getValue().toString())){
                                            reportType.setSelection(i);
                                            break;
                                        }
                                    }
                                }

                                if(snapshot.hasChild("report_details")){
                                    reportDesc.setText(snapshot.child("report_details").getValue().toString());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    Map userInfo = new HashMap<>();

                    switch (view.getId()) {
                        case R.id.fragment_btn_report:
                            userInfo.put(userNode + "/reported_by", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            userInfo.put(userNode + "/reported_at", System.currentTimeMillis());
                            userInfo.put(userNode + "/report_category", reportType.getSelectedItem().toString());
                            userInfo.put(userNode + "/report_details", reportDesc.getText().toString());

                            databaseReference.updateChildren(userInfo).addOnCompleteListener(task1 -> {
                                if(!task1.isSuccessful()){
                                    Toast.makeText(MessageActivity.this, "Failed to Report", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MessageActivity.this, "Reported", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        case R.id.fragment_btn_upload:
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, 1);
                            break;
                    }})
                .setExpanded(true, 900)  // This will enable the expand feature, (similar to android L share dialog)
                .create();


        inputView.setInputListener(input -> {
            Message message = new Message(authCurrentUser.getUid(), user, String.valueOf(input));

            DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
            Bundle extras = getIntent().getExtras();
            final String chatID = extras.getString("chatID");
            final String otherUserId = extras.getString("otherUserId");
            String messagesNode = "Conversations/" + chatID;
            String msgKey = dbRef.child(messagesNode).push().getKey();

            Map updates = new HashMap<>();
            Map msg = new HashMap<>();

            msg.put("sent_by", authCurrentUser.getUid());
            msg.put("content", String.valueOf(input));
            msg.put("date_sent", System.currentTimeMillis());

            updates.put(messagesNode + "/" + msgKey,msg);

            dbRef.updateChildren(updates);

            return true;
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;

            if(resultUri != null){
                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("reportImages").child(userId).child(String.valueOf(System.currentTimeMillis()));
                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(MessageActivity.this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] byteArray = baos.toByteArray();
                UploadTask uploadTask = filepath.putBytes(byteArray);
                uploadTask.addOnFailureListener(e -> Log.e("image upload", e.getMessage()));

                uploadTask.addOnSuccessListener(taskSnapshot -> {

                    filepath.getDownloadUrl().addOnSuccessListener(uri -> {

                        Map userInfo = new HashMap();
                        String userNode = "Reports/" + otherUserId;
                        userInfo.put(userNode + "/reportImageUrl", uri.toString());
                        dbRef.updateChildren(userInfo);

                    });

                    return;
                });
            }

            Log.d("finished upload", String.valueOf(resultUri));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.report:
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatmenu, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference dbRef = FirebaseDatabase.getInstance("https://tinpet-401ae-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        Bundle extras = getIntent().getExtras();
        final String chatID = extras.getString("chatID");
        otherUserId = extras.getString("otherUserId");
        String messageNode = "Conversations/" + chatID ;
        getSupportActionBar().setTitle(extras.getString("pet_name"));

        dbRef.child(messageNode).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    String msgContent = snapshot.child("content").getValue().toString();
                    Long dateSentLong = Long.parseLong(snapshot.child("date_sent").getValue().toString());
                    Date dateSent = new Date(dateSentLong);
                    String sentBy = snapshot.child("sent_by").getValue().toString();

                    dbRef.child("Pets/" + snapshot.child("sent_by").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if(snapshot2.exists()){

                                String name = snapshot2.child("pet_name").getValue().toString();
                                String imageURL;

                                if (snapshot2.child("profileImageUrl").exists())
                                    imageURL = snapshot2.child("profileImageUrl").getValue().toString();
                                else imageURL = null;

                                Author author = new Author(sentBy, name , imageURL, true);
                                Message message = new Message(authCurrentUser.getUid(), author, String.valueOf(msgContent), dateSent);
                                mAdapter.addToStart(message, true);
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
