package com.example.travelapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelapp.Adapter.MessageAdapter;
import com.example.travelapp.model.Chat;
import com.example.travelapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    Users chats_user;

    TextView name_chats_user,status_chats_user;
    CircleImageView image_chats_user;
    ImageButton send_button;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();


        text_send = findViewById(R.id.text_send);

        Intent chats_user_uid = getIntent();
        chats_user = (Users) chats_user_uid.getSerializableExtra("USER_FRIENDS_UID");
        assert chats_user != null;

        name_chats_user = findViewById(R.id.name_chats_user);
        name_chats_user.setText(chats_user.getName());
        image_chats_user = findViewById(R.id.image_chats_user);
        Glide.with(ChatActivity.this).load(chats_user.getImage()).override(40,40).fitCenter().into(image_chats_user);

        status_chats_user = findViewById(R.id.status_chats_user);
        reference.child("Status").child(chats_user.getUid()).child("active").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().equals(true)){
                    status_chats_user.setText("Online");
                } else if (snapshot.getValue().equals(false)){
                    status_chats_user.setText("Offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView = findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readMessage(auth.getUid(), chats_user.getUid(), chats_user.getImage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send_button = findViewById(R.id.button_send);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    sendMessage(auth.getUid(),chats_user.getUid(),msg);
                } else {
                    Toast.makeText(ChatActivity.this, "Can't send empty message!", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });



        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(String myID, String userID, String imageUrl){
        mChat = new ArrayList<>();

        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myID) && chat.getSender().equals(userID) || chat.getReceiver().equals(userID) && chat.getSender().equals(myID)){
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(ChatActivity.this, mChat,imageUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}