package com.example.travelapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.travelapp.Adapter.HomeContentAdapter;
import com.example.travelapp.R;
import com.example.travelapp.home.HomeContent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView lat, lng;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    RecyclerView recyclerView;
    List<HomeContent> contentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View home_fragment_view = inflater.inflate(R.layout.fragment_home, container, false);

        //code new here
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        recyclerView = home_fragment_view.findViewById(R.id.recyclerView);
        
        initData();
        setRecyclerView();

        return home_fragment_view;
        // do not delete
    }

    private void setRecyclerView() {
        HomeContentAdapter contentAdapter = new HomeContentAdapter(contentList);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setHasFixedSize(true);

    }

    private void initData() {

        contentList = new ArrayList<>();

        // Vocabulary
        String numbers = getStringFromArray(getResources().getStringArray(R.array.numbers));
        String vietNumbers = getStringFromArray(getResources().getStringArray(R.array.vietNumbers));
        String word = getStringFromArray(getResources().getStringArray(R.array.word));
        String translation = getStringFromArray(getResources().getStringArray(R.array.translation));

        // TODO: Add hard code information to database
        contentList.add(new HomeContent("Basic Vocabulary", "Numbers", numbers, vietNumbers, "General", word, translation));

        // TODO: Other Homescreen elements
        contentList.add(new HomeContent("Market", "Item", "Price", "List of items", "List of prices", ""));
        contentList.add(new HomeContent("Topic3", "Test1", "Test2", "Test3", "Test4", ""));
        contentList.add(new HomeContent("Topic3", "Test1", "Test2", "Test3", "Test4", ""));
        contentList.add(new HomeContent("Topic3", "Test1", "Test2", "Test3", "Test4", ""));
        contentList.add(new HomeContent("Topic3", "Test1", "Test2", "Test3", "Test4", ""));
        contentList.add(new HomeContent("Topic3", "Test1", "Test2", "Test3", "Test4", ""));
    }

    private String getStringFromArray(String[] resource){
        return Arrays.toString(resource).replaceAll("\\[|\\]","").replaceAll(", ", "\n");
    }
}