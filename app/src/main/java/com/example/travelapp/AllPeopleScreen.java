package com.example.travelapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.travelapp.Adapter.UsersAdapter;
import com.example.travelapp.maps.IBaseGpsListener;
import com.example.travelapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AllPeopleScreen extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    private ListView list_users;
    private ArrayList<Users> usersArrayList;
    private UsersAdapter usersAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people_screen);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();


        list_users = findViewById(R.id.list_users);
        usersArrayList = new ArrayList<>();
        Data();
        usersAdapter = new UsersAdapter(AllPeopleScreen.this,R.layout.listview_users,usersArrayList);
        list_users.setAdapter(usersAdapter);
    }

    private void Data() {
        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersAdapter.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Users people = dataSnapshot.getValue(Users.class);
                    assert people != null;
                    people.setUid(dataSnapshot.getKey());

                        reference.child("Friends").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                for (DataSnapshot dataSnapshot1: snapshot1.getChildren()){
                                    Users check = dataSnapshot1.getValue(Users.class);
                                    assert check != null;
                                    check.setUid(dataSnapshot1.getKey());
                                    Log.d(TAG, "onDataChange: "+check);
                                    if (dataSnapshot.getKey().equals(check.getUid())|| dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())){
                                        usersAdapter.remove(people);
                                    }else {
                                        usersAdapter.add(people);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error1) {

                            }
                        });
//                        usersAdapter.remove(people);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reference.child("Status").child(auth.getCurrentUser().getUid()).child("active").setValue(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.child("Status").child(auth.getCurrentUser().getUid()).child("active").setValue(false);
    }
}