package com.example.travelapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.travelapp.fragment.HomeFragment;
import com.example.travelapp.fragment.MessageFragment;
import com.example.travelapp.fragment.NotifyFragment;
import com.example.travelapp.fragment.ProfileFragment;
import com.example.travelapp.fragment.TranslateFragment;

public class ContainerHomeViewAdapter extends FragmentStateAdapter {

    public ContainerHomeViewAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new MessageFragment();
            case 2:
                return new TranslateFragment();
            case 3:
                return new ProfileFragment();
            case 4:
                return new NotifyFragment();
            case 0:
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
