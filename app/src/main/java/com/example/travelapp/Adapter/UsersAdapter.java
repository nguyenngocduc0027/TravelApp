package com.example.travelapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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

public class UsersAdapter extends ArrayAdapter<Users> {
    private Activity activity;
    private int resource;
    private List<Users> objects;

    public UsersAdapter(@NonNull Activity activity, int resource, @NonNull List<Users> objects) {
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
        TextView name = view.findViewById(R.id.name_user_list);
        TextView gender = view.findViewById(R.id.gender_user_list);
        TextView email = view.findViewById(R.id.email_user_list);
        CircleImageView avatar = view.findViewById(R.id.avatar_user_list);



        Users users = this.objects.get(position);
        name.setText(users.getName());
        gender.setText(users.getGender());
        email.setText(users.getEmail());
        Glide.with(view.getContext()).load(users.getImage()).override(50,50).fitCenter().into(avatar);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();






        ImageButton add_user_request = view.findViewById(R.id.add_user_request);
        add_user_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show();
                reference.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Users usersRequest = dataSnapshot.getValue(Users.class);
                            assert usersRequest != null;
                            usersRequest.setUid(dataSnapshot.getKey());
                            if (usersRequest.getUid().equals(auth.getCurrentUser().getUid())){
                                reference.child("RequestList").child(users.getUid()).child(auth.getCurrentUser().getUid()).setValue(usersRequest);
                            }
                        }
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
