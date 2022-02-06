package com.example.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelapp.maps.IBaseGpsListener;
import com.example.travelapp.model.Users;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity implements IBaseGpsListener  {

    private EditText email,password;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);


        // navigate to register screen
        TextView register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterScreen.class));
            }
        });

        // navigate to forgot password screen
        Button forgot_password = findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgotPassword.class));
            }
        });

        // insert data of edittext
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.pass_login);

        // login
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

//        if (auth.getCurrentUser() != null)
//        {
////            GetLocation();
//            startActivity(new Intent(getApplicationContext(),HomeScreen.class));
//            finish();
//        }

            Button login = findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (email.getEditableText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Email is empty !", Toast.LENGTH_SHORT).show();
                    } else if (password.getEditableText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Password is empty !", Toast.LENGTH_SHORT).show();
                    } else {
                        auth.signInWithEmailAndPassword(email.getEditableText().toString(), password.getEditableText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            reference.child("Users").child(Objects.requireNonNull(auth.getUid())).child("password").setValue(password.getEditableText().toString());
                                            GetLocation();
                                            Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                });
                    }
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
}