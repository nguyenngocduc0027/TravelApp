package com.example.travelapp.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.travelapp.Adapter.RequestAdapter;
import com.example.travelapp.Adapter.UsersAdapter;
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

public class NotifyFragment extends Fragment {

    private ListView list_request;
    private ArrayList<Users> usersArrayList;
    private RequestAdapter requestAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View notify_fragment_view = inflater.inflate(R.layout.fragment_notify, container, false);
        // do not delete
        //code new here

        list_request = notify_fragment_view.findViewById(R.id.list_request);
        usersArrayList = new ArrayList<>();
        Getdata();
        requestAdapter = new RequestAdapter(getActivity(),R.layout.listview_request,usersArrayList);
        list_request.setAdapter(requestAdapter);


        return notify_fragment_view;
    }

    private void Getdata() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("RequestList").child(firebaseAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestAdapter.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Users usersData = dataSnapshot.getValue(Users.class);
                    assert usersData != null;
                    usersData.setUid(dataSnapshot.getKey());
                    requestAdapter.add(usersData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}