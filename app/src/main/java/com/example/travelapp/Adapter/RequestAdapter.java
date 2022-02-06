package com.example.travelapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.travelapp.R;
import com.example.travelapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends ArrayAdapter<Users> {

    private Activity activity;
    private int resource;
    private List<Users> objects;

    public RequestAdapter(@NonNull Activity activity, int resource, @NonNull List<Users> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.activity.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(this.resource,null);

        // code here

        TextView name = view.findViewById(R.id.name_user_request);
        TextView gender = view.findViewById(R.id.gender_user_request);
        TextView email = view.findViewById(R.id.email_user_request);
        CircleImageView avatar = view.findViewById(R.id.avatar_user_request);

        Users users = this.objects.get(position);
        name.setText(users.getName());
        gender.setText(users.getGender());
        email.setText(users.getEmail());
        Glide.with(view.getContext()).load(users.getImage()).override(50,50).fitCenter().into(avatar);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();

        ImageButton cancel_request = view.findViewById(R.id.cancel_request);
        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("RequestList").child(auth.getCurrentUser().getUid()).child(users.getUid()).removeValue();
            }
        });

        ImageButton accept_request = view.findViewById(R.id.accept_request);
        accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("Friends").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reference.child("Friends").child(auth.getCurrentUser().getUid()).child(users.getUid()).setValue(users);

                        reference.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users_add = snapshot.getValue(Users.class);
                                reference.child("Friends").child(users.getUid()).child(users_add.getUid()).setValue(users_add);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        reference.child("RequestList").child(auth.getCurrentUser().getUid()).child(users.getUid()).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });























        return view;
    }
}
