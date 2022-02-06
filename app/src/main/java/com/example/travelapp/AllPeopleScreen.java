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

public class AllPeopleScreen extends AppCompatActivity implements IBaseGpsListener {

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
        GetLocation();


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

    public void GetLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY},1000);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
        } else {ShowLocation();}
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                ShowLocation();
            } else {
                Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void ShowLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000, 1,this);
        } else {
            Toast.makeText(this, "Enable GPS!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        reference.child("Location").child(auth.getUid()).child("lat").setValue(location.getLatitude());
        reference.child("Location").child(auth.getUid()).child("lng").setValue(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        //empty
    }

    @Override
    public void onProviderEnabled(String provider) {
        //empty
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //empty
    }

    @Override
    public void onGpsStatusChanged(int event) {
        //empty
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