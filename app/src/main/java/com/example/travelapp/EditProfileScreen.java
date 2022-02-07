package com.example.travelapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelapp.maps.IBaseGpsListener;
import com.example.travelapp.model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileScreen extends AppCompatActivity implements IBaseGpsListener {

    private CircleImageView avatar;
    private EditText email, name, dob, phone, address, password;
    private Spinner gender;

    private DatePickerDialog datePickerDialog;

    ArrayAdapter<String> genderAdapter;
    String[] list_gender = {"Male","Female"};

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage storage;

    Uri imageUri;
    StorageReference storageReference;
    StorageTask uploadTask;
    String myUri = "";

    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_screen);

        //firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("Users");


        // insert data
        avatar = findViewById(R.id.edit_avatar_profile);
        email = findViewById(R.id.edit_email_profile);
        name = findViewById(R.id.edit_name_profile);
        dob = findViewById(R.id.edit_dob_profile);
        gender = findViewById(R.id.edit_gender_profile);
        phone = findViewById(R.id.edit_phone_profile);
        address = findViewById(R.id.edit_address_profile);
        password = findViewById(R.id.edit_password_profile);
        ImageView pick_edit_avatar_profile = findViewById(R.id.pick_edit_avatar_profile);

        // pick image avatar
        pick_edit_avatar_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(EditProfileScreen.this);
            }
        });

        // date pick dialog
        dob.setInputType(InputType.TYPE_NULL);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                datePickerDialog =  new DatePickerDialog(EditProfileScreen.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // gender spinner
        genderAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,list_gender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderAdapter);

        // cancel
        Button cancel_edit_profile = findViewById(R.id.cancel_edit_profile);
        cancel_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set data
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        reference = database.getReference();
        reference.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = snapshot.getValue(Users.class);
                assert users != null;
                email.setText(users.getEmail());
                name.setText(users.getName());
                dob.setText(users.getDob());
                phone.setText(users.getPhone());
                address.setText(users.getAddress());
                password.setText(users.getPassword());
                Glide.with(EditProfileScreen.this).load(users.getImage()).override(150,150).fitCenter().into(avatar);
                if (users.getGender().equals("Female")){
                    gender.setSelection(1);
                } else {
                    gender.setSelection(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Save data
        Button done_edit_profile = findViewById(R.id.done_edit_profile);
        done_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                auth = FirebaseAuth.getInstance();
                reference = database.getReference().child("Users");
                String email_edit = email.getEditableText().toString();
                String name_edit = name.getEditableText().toString();
                String dob_edit = dob.getEditableText().toString();
                String gender_edit = gender.getSelectedItem().toString();
                String phone_edit = phone.getEditableText().toString();
                String address_edit = address.getEditableText().toString();
                String password_edit = password.getEditableText().toString();
                if (imageUri != null){
                    final ProgressDialog progressDialog = new ProgressDialog(EditProfileScreen.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    final  StorageReference fileRef = storageReference.child(auth.getUid()+".jpg");
                    uploadTask = fileRef.putFile(imageUri);
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if (!task.isSuccessful()){
                                throw  task.getException();
                            }
                            return fileRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downloadUrl = task.getResult();
                                myUri = downloadUrl.toString();
                                Users done = new Users(auth.getUid().toString(),myUri,email_edit,name_edit,dob_edit,gender_edit,phone_edit,address_edit,password_edit);
                                reference.child(auth.getUid()).setValue(done);
                                progressDialog.dismiss();
                                Toast.makeText(EditProfileScreen.this, "Successful !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    reference.child(auth.getCurrentUser().getUid()).child("email").setValue(email_edit);
                    reference.child(auth.getCurrentUser().getUid()).child("name").setValue(name_edit);
                    reference.child(auth.getCurrentUser().getUid()).child("dob").setValue(dob_edit);
                    reference.child(auth.getCurrentUser().getUid()).child("gender").setValue(gender_edit);
                    reference.child(auth.getCurrentUser().getUid()).child("phone").setValue(phone_edit);
                    reference.child(auth.getCurrentUser().getUid()).child("address").setValue(address_edit);
                    reference.child(auth.getCurrentUser().getUid()).child("password").setValue(password_edit);
                    Toast.makeText(EditProfileScreen.this, "Successful !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // access crop image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            avatar.setImageURI(imageUri);
        } else {
            Toast.makeText(EditProfileScreen.this,"Error, Please try again !", Toast.LENGTH_SHORT).show();
        }
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
        reference.child("Location").child(auth.getUid()).child("latitude").setValue(location.getLatitude());
        reference.child("Location").child(auth.getUid()).child("longitude").setValue(location.getLongitude());
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