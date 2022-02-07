package com.example.travelapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.travelapp.GGMaps;
import com.example.travelapp.R;
import com.example.travelapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends ArrayAdapter<Users> {
    private Activity activity;
    private int resource;
    private List<Users> objects;

    public FriendsAdapter(@NonNull Activity activity, int resource, @NonNull List<Users> objects) {
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

        TextView name = view.findViewById(R.id.name_user_friends);
        TextView gender = view.findViewById(R.id.gender_user_friends);
        TextView email = view.findViewById(R.id.email_user_friends);
        CircleImageView avatar = view.findViewById(R.id.avatar_user_friends);

        Users users = this.objects.get(position);
        name.setText(users.getName());
        gender.setText(users.getGender());
        email.setText(users.getEmail());
        Glide.with(view.getContext()).load(users.getImage()).override(50,50).fitCenter().into(avatar);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();


        ImageButton location_user_friends = view.findViewById(R.id.location_user_friends);
        location_user_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(activity, "Click"+users.getUid(), Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(activity, GGMaps.class).putExtra("USER_FRIENDS_UID",users));
            }
        });

        FrameLayout users_friends = view.findViewById(R.id.users_friends);
        users_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Friends", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
