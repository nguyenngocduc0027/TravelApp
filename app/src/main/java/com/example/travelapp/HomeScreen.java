package com.example.travelapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelapp.Adapter.ContainerHomeViewAdapter;
import com.example.travelapp.maps.IBaseGpsListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeScreen extends AppCompatActivity implements IBaseGpsListener {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    private String link_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        ViewPager2 container_home_view = findViewById(R.id.container_home_view);
        ContainerHomeViewAdapter containerHomeViewAdapter = new ContainerHomeViewAdapter(this);
        container_home_view.setUserInputEnabled(false);
        container_home_view.setAdapter(containerHomeViewAdapter);

        BottomNavigationView bottomNavControl = findViewById(R.id.bottom_nav_control);
        bottomNavControl.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottom_nav_home){
                    container_home_view.setCurrentItem(0);
                } else if (item.getItemId() == R.id.bottom_nav_message){
                    container_home_view.setCurrentItem(1);
                } else if (item.getItemId() == R.id.bottom_nav_translate){
                    container_home_view.setCurrentItem(2);
                } else if (item.getItemId() == R.id.bottom_nav_manage_account){
                    container_home_view.setCurrentItem(3);
                }
                return true;
            }
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference.child("Status").child(auth.getCurrentUser().getUid()).child("active").setValue(true);
        GetLocation();



        CircleImageView avatar_home = findViewById(R.id.avatar_home);
        reference.child("Users").child(auth.getCurrentUser().getUid()).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                link_avatar = snapshot.getValue(String.class);
                Glide.with(HomeScreen.this).load(link_avatar).override(50,50).fitCenter().into(avatar_home);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeScreen.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
            }
        });


        TextView name_home = findViewById(R.id.name_home);
        reference.child("Users").child(auth.getCurrentUser().getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                name_home.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeScreen.this, "Error Loading Name", Toast.LENGTH_SHORT).show();
            }
        });


        ImageButton notification_home = findViewById(R.id.notification_home);
        notification_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container_home_view.setCurrentItem(4);
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

//    @Override
//    protected void onStop() {
//        super.onStop();
//        reference.child("Location").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("active").setValue(false);
//    }
}