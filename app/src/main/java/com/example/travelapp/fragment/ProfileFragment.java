package com.example.travelapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelapp.EditProfileScreen;
import com.example.travelapp.LoginScreen;
import com.example.travelapp.R;
import com.example.travelapp.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View profile_fragment_view = inflater.inflate(R.layout.fragment_profile, container, true); // do not delete
        //code new here

        // get data
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        //show avatar
        CircleImageView avatar_profile = profile_fragment_view.findViewById(R.id.avatar_profile);
        databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String avatar_profile_link = snapshot.getValue(String.class);
                Glide.with(ProfileFragment.this).load(avatar_profile_link).fitCenter().override(150,150).into(avatar_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //show name
        TextView name_profile = profile_fragment_view.findViewById(R.id.name_profile);
        databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                name_profile.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //show gender
        TextView gender_profile = profile_fragment_view.findViewById(R.id.gender_profile);
        databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("gender").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String gender = snapshot.getValue(String.class);
                gender_profile.setText(gender);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //show email
        TextView email_profile = profile_fragment_view.findViewById(R.id.email_profile);
        databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.getValue(String.class);
                email_profile.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // show dob
        TextView dob_profile = profile_fragment_view.findViewById(R.id.dob_profile);
        databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("dob").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dob = snapshot.getValue(String.class);
                dob_profile.setText(dob);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // show phone
        TextView phone_profile = profile_fragment_view.findViewById(R.id.phone_profile);
        databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phone = snapshot.getValue(String.class);
                phone_profile.setText(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // show dob
        TextView address_profile = profile_fragment_view.findViewById(R.id.address_profile);
        databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String address = snapshot.getValue(String.class);
                address_profile.setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //logout
        Button logout = profile_fragment_view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Status").child(firebaseAuth.getCurrentUser().getUid()).child("active").setValue(false);
                startActivity(new Intent(inflater.getContext(), LoginScreen.class));
                FirebaseAuth.getInstance().signOut();
            }
        });

        ImageButton edit_profile = profile_fragment_view.findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), EditProfileScreen.class));
            }
        });

        return profile_fragment_view;
    }
}