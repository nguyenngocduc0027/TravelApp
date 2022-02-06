package com.example.travelapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelapp.model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterScreen extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        
        //firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // insert data
        avatar = findViewById(R.id.avatar_register);
        email = findViewById(R.id.email_register);
        name = findViewById(R.id.name_register);
        dob = findViewById(R.id.dob_register);
        gender = findViewById(R.id.gender_register);
        phone = findViewById(R.id.phone_register);
        address = findViewById(R.id.address_register);
        password = findViewById(R.id.password_register);
        ImageView pick_avater_register = findViewById(R.id.pick_avater_register);


        // pick image avatar
        pick_avater_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(RegisterScreen.this);
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

                datePickerDialog =  new DatePickerDialog(RegisterScreen.this,
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

        // Register
        Button register = findViewById(R.id.done_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                auth = FirebaseAuth.getInstance();
                reference = database.getReference().child("Users");
                String email_reg = email.getEditableText().toString();
                String name_reg = name.getEditableText().toString();
                String dob_reg = dob.getEditableText().toString();
                String gender_reg = gender.getSelectedItem().toString();
                String phone_reg = phone.getEditableText().toString();
                String address_reg = address.getEditableText().toString();
                String password_reg = password.getEditableText().toString();
                if (!email_reg.isEmpty()) {
                    if (!name_reg.isEmpty()) {
                        if (!dob_reg.isEmpty()) {
                            if (!gender_reg.isEmpty()) {
                                if (!phone_reg.isEmpty()) {
                                    if (!address_reg.isEmpty()) {
                                        if (!password_reg.isEmpty()) {
                                            auth.createUserWithEmailAndPassword(email.getEditableText().toString(), password.getEditableText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                if (imageUri != null) {
                                                                    final StorageReference fileRef = storageReference.child("Users").child(auth.getUid() + ".jpg");
                                                                    uploadTask = fileRef.putFile(imageUri);
                                                                    uploadTask.continueWithTask(new Continuation() {
                                                                        @Override
                                                                        public Object then(@NonNull Task task) throws Exception {
                                                                            if (!task.isSuccessful()) {
                                                                                throw task.getException();
                                                                            }
                                                                            return fileRef.getDownloadUrl();
                                                                        }
                                                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Uri> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Uri downloadUrl = task.getResult();
                                                                                myUri = downloadUrl.toString();
                                                                                Users users = new Users(auth.getUid().toString(),myUri, email_reg, name_reg, dob_reg, gender_reg, phone_reg, address_reg, password_reg);
                                                                                reference.child(auth.getUid()).setValue(users);
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    Users users = new Users(auth.getUid().toString(),"https://firebasestorage.googleapis.com/v0/b/travelapp-project3.appspot.com/o/null_avatar.jpg?alt=media&token=28fe29b9-224d-4517-8180-8c22b53cfda3"
                                                                            , email_reg, name_reg, dob_reg, gender_reg, phone_reg, address_reg, password_reg);
                                                                    reference.child(auth.getUid()).setValue(users);
                                                                }
                                                            } else {
                                                                Toast.makeText(RegisterScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }
                                                        }
                                                    });

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Password empty", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Address null", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Phone number empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "What is your gender ?", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Pick date of birth now !", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter full name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(RegisterScreen.this, "Email Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginScreen.class));
                finish();
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
            Toast.makeText(RegisterScreen.this,"Error, Please try again !", Toast.LENGTH_SHORT).show();
        }
    }
}