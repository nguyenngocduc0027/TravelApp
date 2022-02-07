package com.example.travelapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelapp.maps.IBaseGpsListener;
import com.example.travelapp.model.MyLocation;
import com.example.travelapp.model.Users;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GGMaps extends AppCompatActivity implements OnMapReadyCallback, IBaseGpsListener {

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference reference;

    Users friends_user;

    LocationManager manager;
    Marker myMarker;

    MyLocation myLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ggmaps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("Status").child(auth.getCurrentUser().getUid()).child("active").setValue(true);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);


        Intent friend_user_uid = getIntent();
        friends_user = (Users) friend_user_uid.getSerializableExtra("USER_FRIENDS_UID");
        assert friends_user != null;
        
        getLocationUpdates();
//        readChanges();

    }

//    private void readChanges() {
//        reference.child("Location").child(friends_user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    try {
//                        myLocation = snapshot.getValue(MyLocation.class);
//                        if (myLocation != null){
//                            myMarker.setPosition(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()));
//                        }
//                    } catch (Exception e){
//                        Toast.makeText(GGMaps.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void getLocationUpdates() {
        if (manager != null){
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1,this);
                }
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocationUpdates();
            }
        }
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

        reference.child("Location").child(friends_user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    myLocation = snapshot.getValue(MyLocation.class);
                    if (myLocation != null){
                        LatLng here = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(here).title(friends_user.getName()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here,15));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        LatLng sydney = new LatLng(21.00680495566101,105.84345786268916);
//        myMarker = googleMap.addMarker(new MarkerOptions().position(sydney).title(friends_user.getName()));
//        googleMap.getUiSettings().setAllGesturesEnabled(true);
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }


    @Override
    public void onLocationChanged(Location location) {
        
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}