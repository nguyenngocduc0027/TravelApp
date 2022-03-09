package com.example.travelapp.fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.travelapp.Adapter.FriendsAdapter;
import com.example.travelapp.AllPeopleScreen;
import com.example.travelapp.R;
import com.example.travelapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MessageFragment extends Fragment {
    private ListView list_friends;
    private ArrayList<Users> usersArrayList;
    private FriendsAdapter friendsAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View message_fragment_view = inflater.inflate(R.layout.fragment_message, container, false);// do not delete
        //code new here


        list_friends = message_fragment_view.findViewById(R.id.list_message_user);
        usersArrayList = new ArrayList<>();
        Getdata();
        friendsAdapter = new FriendsAdapter(getActivity(),R.layout.listview_friends,usersArrayList);
        list_friends.setAdapter(friendsAdapter);


        ImageButton button_add_people= message_fragment_view.findViewById(R.id.button_add_people);
        button_add_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), AllPeopleScreen.class));
            }
        });
        return message_fragment_view;
    }

    private void Getdata() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("Friends").child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friendsAdapter.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Users usersFriends = dataSnapshot.getValue(Users.class);
                            assert usersFriends != null;
                            usersFriends.setUid(dataSnapshot.getKey());
                            friendsAdapter.add(usersFriends);
//                            databaseReference.child("Status").child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
////                                            Log.d(TAG, "onDataChange: "+snapshot1.child("active").getValue());
//                                            if (snapshot1.child("active").getValue().equals(true)){
//                                                friendsAdapter.add(usersFriends);
//                                            } else if (snapshot1.child("active").getValue().equals(false)){
//                                                friendsAdapter.remove(usersFriends);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}